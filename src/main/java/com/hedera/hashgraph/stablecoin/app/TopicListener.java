package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.sdk.mirror.MirrorClient;
import com.hedera.hashgraph.sdk.mirror.MirrorConsensusTopicQuery;
import com.hedera.hashgraph.sdk.mirror.MirrorSubscriptionHandle;
import com.hedera.hashgraph.stablecoin.app.emitter.Emitter;
import com.hedera.hashgraph.stablecoin.app.handler.ApproveAllowanceTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ApproveExternalTransferTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.BurnTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ChangeComplianceManagerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ChangeEnforcementManagerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ChangeSupplyManagerTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ClaimOwnershipTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ConstructTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.DecreaseAllowanceTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ExternalTransferFromTransactionHandler;
import com.hedera.hashgraph.stablecoin.app.handler.ExternalTransferTransactionHandler;
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
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

import org.bouncycastle.math.ec.rfc8032.Ed25519;

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
        entry(DataCase.CHANGECOMPLIANCEMANAGER, new ChangeComplianceManagerTransactionHandler()),
        entry(DataCase.CHANGEENFORCEMENTMANAGER, new ChangeEnforcementManagerTransactionHandler()),
        entry(DataCase.FREEZE, new FreezeTransactionHandler()),
        entry(DataCase.UNFREEZE, new UnfreezeTransactionHandler()),
        entry(DataCase.WIPE, new WipeTransactionHandler()),
        entry(DataCase.SETKYCPASSED, new SetKycPassedTransactionHandler()),
        entry(DataCase.UNSETKYCPASSED, new UnsetKycPassedTransactionHandler()),
        entry(DataCase.INCREASEALLOWANCE, new IncreaseAllowanceTransactionHandler()),
        entry(DataCase.DECREASEALLOWANCE, new DecreaseAllowanceTransactionHandler()),
        entry(DataCase.APPROVEEXTERNALTRANSFER, new ApproveExternalTransferTransactionHandler()),
        entry(DataCase.EXTERNALTRANSFER, new ExternalTransferTransactionHandler()),
        entry(DataCase.EXTERNALTRANSFERFROM, new ExternalTransferFromTransactionHandler())
    );

    @Nullable
    private final MirrorClient mirrorClient;

    private final ConsensusTopicId topicId;

    @Nullable
    private final TransactionRepository transactionRepository;

    private final Emitter emitter = new Emitter();

    @Nullable
    private MirrorSubscriptionHandle handle;

    public TopicListener(State state, @Nullable MirrorClient mirrorClient, ConsensusTopicId topicId, @Nullable TransactionRepository transactionRepository) {
        this.state = state;
        this.mirrorClient = mirrorClient;
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
        if (mirrorClient == null) {
            return;
        }

        System.out.println("listening on topic " + topicId + " from " + state.getTimestamp());

        handle = new MirrorConsensusTopicQuery()
            .setTopicId(topicId)
            // add 1 ns so we don't pull in the same message
            .setStartTime(state.getTimestamp().plusNanos(1))
            .subscribe(mirrorClient, topicMessage -> {
                try {
                    handleTransaction(topicMessage.consensusTimestamp, Transaction.parseFrom(topicMessage.message));
                } catch (InvalidProtocolBufferException e) {
                    // log the failure, this is a parsing failure of an incoming message
                    // likely someone posted to our topic by mistake
                    e.printStackTrace();
                } catch (SQLException e) {
                    // exception with persistence, not recoverable
                    // a programming error or the database is gone
                    throw new RuntimeException(e);
                }
            }, e -> {
                System.err.println("topic listener failed: " + e.getMessage());

                e.printStackTrace();

                try {
                    // wait 1s before we try again
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    // continue on
                }

                // listener failed, restart the listener
                startListening();
            });
    }

    private static boolean verifySignature(Ed25519PublicKey publicKey, byte[] message, byte[] signature) {
        // NOTE: The Hedera SDK v1 does not directly expose the verify method
        return Ed25519.verify(signature, 0, publicKey.toBytes(), 0, message, 0, message.length);
    }

    void handleTransaction(Instant consensusTimestamp, Transaction transaction) throws InvalidProtocolBufferException, SQLException {
        var transactionBodyBytes = transaction.getBody();
        var transactionBody = TransactionBody.parseFrom(transactionBodyBytes);
        var transactionId = TransactionId.parse(transactionBody.getTransactionId());

        @SuppressWarnings("unchecked")
        var transactionHandler = (TransactionHandler<Object>) transactionHandlers.get(transactionBody.getDataCase());

        if (transactionHandler == null) {
            throw new RuntimeException("transaction handler not implemented for " + transactionBody.getDataCase());
        }

        var transactionArguments = transactionHandler.parseArguments(transactionBody);

        @Var var generateReceipt = false;

        @Var Status transactionStatus;

        try {
            // check that transaction ID and its caller property were set

            if (transactionId == null) {
                throw new StableCoinPreCheckException(Status.TRANSACTION_ID_NOT_SET);
            }

            if (transactionId.address.isZero()) {
                throw new StableCoinPreCheckException(Status.CALLER_NOT_SET);
            }

            // check that the transaction ID is not expired

            if (transactionId.isExpired(consensusTimestamp)) {
                throw new StableCoinPreCheckException(Status.TRANSACTION_EXPIRED);
            }

            // try to add the transaction ID to the state
            // and flag if its been added before

            if (state.hasTransactionId(transactionId)) {
                throw new StableCoinPreCheckException(Status.TRANSACTION_DUPLICATE);
            }

            var caller = transactionId.address;
            var signature = transaction.getSignature().toByteArray();

            generateReceipt = true;

            // verify that this transaction was signed by the identified caller

            if (!verifySignature(caller.publicKey, transactionBodyBytes.toByteArray(), signature)) {
                throw new StableCoinPreCheckException(Status.INVALID_SIGNATURE);
            }

            // attempt to handle the transaction
            transactionHandler.handle(state, caller, transactionArguments);
            transactionStatus = Status.OK;
        } catch (StableCoinPreCheckException e) {
            // when a pre-check validation failure happens, we still need to log the
            // transaction but the status is adjusted
            transactionStatus = e.status;
        }

        // state has now transitioned
        state.setTimestamp(consensusTimestamp);

        if (generateReceipt) {
            // record the transaction receipt
            state.addTransactionReceipt(transactionId, new TransactionReceipt(
                consensusTimestamp, transactionId, transactionStatus));
        }

        // persist the transaction to our database if there is a configured
        // transaction repository available
        if (transactionRepository != null) {
            transactionRepository.bindTransaction(
                consensusTimestamp,
                transactionId,
                transactionStatus,
                transactionBody.getDataCase(),
                transactionArguments
            );
        }

        if (transactionStatus.equals(Status.OK)) {
            // emit any events for the transaction
            emitter.emit(transactionBody.getDataCase(), state, transactionId, transactionArguments);
        }
    }
}
