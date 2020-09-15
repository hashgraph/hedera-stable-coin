package com.hedera.hashgraph.stablecoin.app;

import java.time.Instant;
import java.util.Objects;

import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class TransactionReceipt implements Comparable<TransactionReceipt> {
    public final Instant consensusAt;

    public final TransactionId transactionId;

    public final Status status;

    public TransactionReceipt(Instant consensusAt, TransactionId transactionId, Status status) {
        this.consensusAt = consensusAt;
        this.transactionId = transactionId;
        this.status = status;
    }

    public boolean isExpired() {
        return transactionId.isExpired();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionReceipt)) {
            return false;
        }

        var other = (TransactionReceipt) o;

        return this.transactionId.equals(other.transactionId) &&
            this.status.equals(other.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, status);
    }

    @Override
    public int compareTo(TransactionReceipt other) {
        return transactionId.compareTo(other.transactionId);
    }
}
