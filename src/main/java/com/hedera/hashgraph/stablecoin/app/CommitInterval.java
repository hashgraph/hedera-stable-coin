package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import io.github.cdimascio.dotenv.Dotenv;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;

public class CommitInterval {
    private final int interval;

    private final Thread commitThread;

    private final State state;

    private final SnapshotManager snapshotManager;

    private final TransactionRepository transactionRepository;

    @Nullable
    private Instant lastCommitTime;

    public CommitInterval(Dotenv env, State state, TransactionRepository transactionRepository, SnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
        this.state = state;
        this.transactionRepository = transactionRepository;

        // how often do we write a new snapshot
        interval = Integer.parseInt(env.get("HSC_COMMIT_INTERVAL", "1")) * 1000;

        commitThread = new Thread(this::onInterval);
        commitThread.start();
    }

    private void onInterval() {
        while (true) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                // thread was interrupted
                break;
            }

            state.lock();

            try {
                if (state.getTimestamp().isBefore(Instant.EPOCH)) {
                    // nothing has happened yet
                    continue;
                }

                if (lastCommitTime != null && lastCommitTime.equals(state.getTimestamp())) {
                    // we just wrote this state
                    // nothing has been happening
                    continue;
                }

                transactionRepository.execute();

                snapshotManager.write();

                snapshotManager.prunePrevious();

                lastCommitTime = state.getTimestamp();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                state.unlock();
            }
        }
    }
}
