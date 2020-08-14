package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.SubscriptionHandle;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.hedera.hashgraph.stablecoin.handler.*;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody.DataCase;

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

    private SubscriptionHandle handle;

    public TopicListener(State state, Client client, TopicId topicId) {
        this.state = state;
        this.client = client;
        this.topicId = topicId;
    }

    public synchronized void stopListening() {
        if (handle != null) {
            handle.unsubscribe();
            handle = null;
        }
    }

    public synchronized void startListening() {
        // todo: set startTime to resume from last state snapshot

        handle = new TopicMessageQuery()
            .setTopicId(topicId)
            .setStartTime(Instant.EPOCH)
            .subscribe(client, topicMessage -> {
                // noinspection TryWithIdenticalCatches
                try {
                    handleTransaction(Transaction.parseFrom(topicMessage.contents));
                } catch (InvalidProtocolBufferException e) {
                    // received an invalid message from the stream
                    // todo: log the parsing failure
                    e.printStackTrace();
                } catch (Exception e) {
                    // fixme: once we start logging transactions as failed
                    //        this branch should not happen
                    e.printStackTrace();
                }
            });
    }

    void handleTransaction(Transaction transaction) throws InvalidProtocolBufferException {
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

        // continue on to process the body
        transactionHandlers.get(transactionBody.getDataCase())
            .handle(state, caller, transactionBody);
    }
}
