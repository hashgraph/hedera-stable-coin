package com.hedera.hashgraph.stablecoin.app;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class TransactionReceipt {
    public final Instant consensusAt;

    public final Address caller;

    public final TransactionId transactionId;

    public final Status status;

    public TransactionReceipt(Instant consensusAt, Address caller, TransactionId transactionId, Status status) {
        this.consensusAt = consensusAt;
        this.caller = caller;
        this.transactionId = transactionId;
        this.status = status;
    }

    public boolean isExpired(Instant fromTimestamp) {
        return ChronoUnit.MINUTES.between(transactionId.validStart, fromTimestamp) > 2;
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
}
