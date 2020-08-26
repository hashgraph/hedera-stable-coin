package com.hedera.hashgraph.stablecoin;

import com.google.common.base.Stopwatch;
import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.stablecoin.proto.Transaction;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Benchmark {
    private Benchmark() {
    }

    public static void runBenchmark(State state, Client client, File file, int count, @Var TopicId topicId, PrivateKey operatorKey) throws IOException, HederaReceiptStatusException, TimeoutException, HederaPreCheckStatusException {
        var transactionsRead = readTransactions(file);

        var topicListener = new TopicListener(state, client, topicId);

        Stopwatch timeKeeper = Stopwatch.createUnstarted();

        timeKeeper.start();

        processTransfers(topicListener, transactionsRead);

        timeKeeper.stop();

        System.out.println("Total Execution Time: " + timeKeeper);
        System.out.println("Transfers per second: " + ((count + 21) / ((double) timeKeeper.elapsed(TimeUnit.MILLISECONDS) / 1000)));

        topicId = Objects.requireNonNull(createTopicId(client, operatorKey));

        System.out.println("Topic Id: " + topicId);

        runHcsBenchmark(state, client, topicId, transactionsRead, count);
    }

    static List<Transaction> readTransactions(File file) throws IOException {
        var reader = new DataInputStream(new FileInputStream(file));

        var transactionsRead = new ArrayList<Transaction>();

        while (reader.available() > 0) {
            var length = reader.readInt();
            var tx = com.hedera.hashgraph.stablecoin.proto.Transaction.parseFrom(reader.readNBytes(length));
            transactionsRead.add(tx);
        }

        return transactionsRead;
    }

    static void processTransfers(TopicListener topicListener, List<Transaction> transactionsRead) throws IOException {
        for (var tx : transactionsRead) {
            topicListener.handleTransaction(tx);
        }
    }

    @Nullable
    static TopicId createTopicId(Client client, PrivateKey operatorKey) throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException {
        return new TopicCreateTransaction()
            .setAdminKey(operatorKey)
            .setNodeAccountId(AccountId.fromString("0.0.4"))
            .execute(client)
            .transactionId
            .getReceipt(client)
            .topicId;
    }

    static void runHcsBenchmark(State state, Client client, TopicId topicId, List<Transaction> transactionsRead, int count) throws IOException, TimeoutException, HederaPreCheckStatusException {
        new TopicListener(state, client, topicId).startListening();

        Stopwatch timeKeeper = Stopwatch.createUnstarted();

        timeKeeper.start();

        sendTransactions(client, topicId, transactionsRead);

        timeKeeper.stop();

        System.out.println("Total Execution Time: " + timeKeeper);
        System.out.println("Transfers per second: " + ((count + 21) / ((double) timeKeeper.elapsed(TimeUnit.MILLISECONDS) / 1000)));


    }

    static void sendTransactions(Client client, TopicId topicId, List<Transaction> transactionsRead) throws IOException, TimeoutException, HederaPreCheckStatusException {
        @Var var count = 1;

        for (Transaction tx : transactionsRead) {

            new TopicMessageSubmitTransaction()
                .setTopicId(topicId)
                .setMessage(tx.toByteArray())
                .setNodeAccountId(AccountId.fromString("0.0.4"))   // Can remove this later
                .execute(client);

            System.out.println("message " + count + " sent!");
            count ++;
        }
    }
}
