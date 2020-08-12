package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.SubscriptionHandle;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageQuery;
import com.hedera.hashgraph.stablecoin.handler.MintTransactionHandler;
import com.hedera.hashgraph.stablecoin.handler.TransferTransactionHandler;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody.DataCase;
import com.hedera.hashgraph.stablecoin.handler.ConstructTransactionHandler;
import com.hedera.hashgraph.stablecoin.handler.TransactionHandler;

import java.time.Instant;
import java.util.HashMap;

/**
 * Listen to the topic on the Hedera Consensus Service (HCS) and handle
 * incoming transactions.
 */
public final class TopicListener {
    private final State state;

    private final HashMap<DataCase, TransactionHandler<?>> transactionHandlers;

    private final Client client;

    private final TopicId topicId;

    private SubscriptionHandle handle;

    public TopicListener(State state, Client client, TopicId topicId) {
        this.state = state;
        this.client = client;
        this.topicId = topicId;

        transactionHandlers = new HashMap<>();
        transactionHandlers.put(DataCase.CONSTRUCT, new ConstructTransactionHandler());
        transactionHandlers.put(DataCase.TRANSFER, new TransferTransactionHandler());
        transactionHandlers.put(DataCase.MINT, new MintTransactionHandler());
    }

    public void startListening() {
        // todo: set startTime to resume from last state snapshot

        handle = new TopicMessageQuery()
            .setTopicId(topicId)
            .setStartTime(Instant.EPOCH)
            .subscribe(client, topicMessage -> {
                try {
                    handleTransaction(Transaction.parseFrom(topicMessage.contents));
                } catch (Exception e) {
                    // received an invalid message from the stream
                    // fixme: log this better
                    e.printStackTrace();
                }
            });
    }

    void handleTransaction(Transaction transaction) throws InvalidProtocolBufferException {
        var transactionBodyBytes = transaction.getBody();
        var transactionBody = TransactionBody.parseFrom(transactionBodyBytes);
        var caller = new Address(transactionBody.getCaller());

        // verify that this transaction was signed by the identified caller
        if (!caller.publicKey.verify(transactionBodyBytes.toByteArray(), transaction.getSignature().toByteArray())) {
            // fixme: flag this transaction as failed with <INVALID_SIGNATURE>
            throw new IllegalStateException("invalid signature");
        }

        // continue on to process the body
        transactionHandlers.get(transactionBody.getDataCase()).handle(state, caller, transactionBody);
    }
}
