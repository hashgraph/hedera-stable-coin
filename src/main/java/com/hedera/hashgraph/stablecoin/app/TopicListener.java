package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.SubscriptionHandle;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.hedera.hashgraph.stablecoin.app.handler.ApproveAllowanceTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.BurnTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ChangeAssetProtectionManagerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ChangeSupplyManagerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ClaimOwnershipTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ConstructTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.DecreaseAllowanceTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.FreezeTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.IncreaseAllowanceTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.MintTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ProposeOwnerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.SetKycPassedTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.StableCoinPreCheckException;
import com.hedera.hashgraph.stablecoin.app.handler.TransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.TransferFromTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.TransferTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.UnfreezeTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.UnsetKycPassedTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.WipeTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody.DataCase;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Listen to the topic on the Hedera Consensus Service (HCS) and handle
 * incoming transactions.
 */
public final class TopicListener {
    private final State state;

    private final Map<DataCase, TransactionHandler<?>> transactionHandlers = Map.ofEntries(
        entry(DataCase.CONSTRUCT, new ConstructTransactionHandler()),
        entry(DataCase.APPROVE, new ApproveAllowanceTransactionHandler()),
        entry(DataCase.MINT, new MintTransactionHandler()),
        entry(DataCase.BURN, new BurnTransactionHandler()),
        entry(DataCase.TRANSFER, new TransferTransactionHandler()),
        entry(DataCase.TRANSFERFROM, new TransferFromTransactionHandler()),
        entry(DataCase.PROPOSEOWNER, new ProposeOwnerTransactionHandler()),
        entry(DataCase.CLAIMOWNERSHIP, new ClaimOwnershipTransactionHandler()),
        entry(DataCase.CHANGESUPPLYMANAGER, new ChangeSupplyManagerTransactionHandler()),
        entry(DataCase.CHANGEASSETPROTECTIONMANAGER, new ChangeAssetProtectionManagerTransactionHandler()),
        entry(DataCase.FREEZE, new FreezeTransactionHandler()),
        entry(DataCase.UNFREEZE, new UnfreezeTransactionHandler()),
        entry(DataCase.WIPE, new WipeTransactionHandler()),
        entry(DataCase.SETKYCPASSED, new SetKycPassedTransactionHandler()),
        entry(DataCase.UNSETKYCPASSED, new UnsetKycPassedTransactionHandler()),
        entry(DataCase.INCREASEALLOWANCE, new IncreaseAllowanceTransactionHandler()),
        entry(DataCase.DECREASEALLOWANCE, new DecreaseAllowanceTransactionHandler())
    );

    private final Client client;

    private final TopicId topicId;

    @Nullable
    private SubscriptionHandle handle;

    @Nullable
    private final TransactionRepository transactionRepository;

    public TopicListener(State state, Client client, TopicId topicId, @Nullable TransactionRepository transactionRepository) {
        this.state = state;
        this.client = client;
        this.topicId = topicId;
        this.transactionRepository = transactionRepository;
    }

    public synchronized void stopListening() {
        if (handle != null) {
            handle.unsubscribe();
            handle = null;
        }
    }

    public synchronized void startListening() {
        System.out.println("listening on topic " + topicId + " from " + state.getTimestamp());

        handle = new TopicMessageQuery()
            .setTopicId(topicId)
            // add 1 ns so we don't pull in the same message
            .setStartTime(state.getTimestamp().plusNanos(1))
            .subscribe(client, topicMessage -> {
                try {
                    handleTransaction(topicMessage.consensusTimestamp, Transaction.parseFrom(topicMessage.contents));
                } catch (InvalidProtocolBufferException e) {
                    // log the failure, this is a parsing failure of an incoming message
                    // likely someone posted to our topic by mistake
                    e.printStackTrace();
                } catch (SQLException e) {
                    // exception with persistence, not recoverable
                    // a programming error or the database is gone
                    throw new RuntimeException(e);
                }
            });
    }

    void handleTransaction(Instant consensusTimestamp, Transaction transaction) throws InvalidProtocolBufferException, SQLException {
        var transactionBodyBytes = transaction.getBody();
        var transactionBody = TransactionBody.parseFrom(transactionBodyBytes);
        var caller = new Address(transactionBody.getCaller());

        if (caller.isZero()) {
            // todo: when transaction logging is added, log this as a failed transaction
            throw new IllegalStateException("validation failed with status " + Status.CALLER_NOT_SET);
        }

        var signature = transaction.getSignature().toByteArray();

        // verify that this transaction was signed by the identified caller
        if (!caller.publicKey.verify(transactionBodyBytes.toByteArray(), signature)) {
            // todo: when transaction logging is added, log this as a failed transaction
            throw new IllegalStateException("validation failed with status " + Status.INVALID_SIGNATURE);
        }

        @SuppressWarnings("unchecked")
        var transactionHandler = (TransactionHandler<Object>) transactionHandlers.get(transactionBody.getDataCase());

        if (transactionHandler == null) {
            throw new IllegalStateException("unimplemented transaction type " + transactionBody.getDataCase());
        }

        Status transactionStatus;

        var transactionArguments = transactionHandler.parseArguments(transactionBody);

        try {
            // attempt to handle the transaction
            transactionHandler.handle(state, caller, transactionArguments);

            // state has now successfully transitioned
            transactionStatus = Status.OK;
            state.setTimestamp(consensusTimestamp);
        } catch (StableCoinPreCheckException e) {
            // when a pre-check validation failure happens, we still need to log the
            // transaction but the status is adjusted
            transactionStatus = e.status;
        }

        // persist the transaction to our database if there is a configured
        // transaction repository available
        if (transactionRepository != null) {
            transactionRepository.bindTransaction(
                consensusTimestamp,
                caller,
                transactionStatus,
                transactionBody.getDataCase(),
                transactionArguments
            );
        }
    }
}
