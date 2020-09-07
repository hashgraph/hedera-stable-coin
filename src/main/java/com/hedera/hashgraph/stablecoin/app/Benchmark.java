package com.hedera.hashgraph.stablecoin.app;

import com.google.common.base.Stopwatch;
import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import com.hedera.hashgraph.stablecoin.proto.Transaction;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Benchmark {
    private final State state;

    private final TransactionRepository transactionRepository;

    private final File inputFile;

    public Benchmark(State state, TransactionRepository transactionRepository, File inputFile) {
        this.state = state;
        this.transactionRepository = transactionRepository;
        this.inputFile = inputFile;
    }

    private static List<Transaction> readTransactions(File file) throws IOException {
        var reader = new DataInputStream(new FileInputStream(file));
        var txn = new ArrayList<Transaction>();

        while (reader.available() > 0) {
            var length = reader.readInt();
            var tx = com.hedera.hashgraph.stablecoin.proto.Transaction.parseFrom(reader.readNBytes(length));

            txn.add(tx);
        }

        return txn;
    }

    static void processTransactions(TopicListener topicListener, TransactionRepository transactionRepository, List<Transaction> transactions) throws IOException, SQLException {
        @Var var fakeConsensusTimestamp = Instant.EPOCH;
        @Var var counter = 0;

        for (var tx : transactions) {
            fakeConsensusTimestamp = fakeConsensusTimestamp.plusNanos(1);
            counter += 1;

            if ((counter % 500) == 0) {
                transactionRepository.execute();
            }

            topicListener.handleTransaction(fakeConsensusTimestamp, tx);
        }

        transactionRepository.execute();
    }

    public void run() throws IOException, SQLException {
        var transactions = readTransactions(inputFile);

        var topicListener = new TopicListener(state, null, new ConsensusTopicId(0, 0, 0), transactionRepository);

        Stopwatch timeKeeper = Stopwatch.createUnstarted();

        timeKeeper.start();

        processTransactions(topicListener, transactionRepository, transactions);

        timeKeeper.stop();

        System.out.println("Total Transactions      : " + transactions.size());
        System.out.println("Total Execution Time    : " + timeKeeper);
        System.out.println("Transactions per second : " + (((double) transactions.size()) / ((double) timeKeeper.elapsed(TimeUnit.MILLISECONDS) / 1000)));
    }
}
