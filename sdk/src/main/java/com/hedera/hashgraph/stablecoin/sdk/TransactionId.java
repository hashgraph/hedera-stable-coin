package com.hedera.hashgraph.stablecoin.sdk;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TransactionId implements Comparable<TransactionId> {
    public final Address address;

    public final Instant validStart;

    public TransactionId(Address address, Instant validStart) {
        this.address = address;
        this.validStart = validStart;
    }

    public static TransactionId parse(com.hedera.hashgraph.stablecoin.proto.TransactionId id) {
        var address = new Address(id.getAddress());
        var validStart = Instant.ofEpochSecond(0, id.getValidStart());

        return new TransactionId(address, validStart);
    }

    public boolean isExpired() {
        return ChronoUnit.MINUTES.between(validStart, Instant.now()) > 2;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionId)) {
            return false;
        }

        var other = (TransactionId) o;

        return this.validStart.equals(other.validStart) &&
            this.address.equals(other.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, validStart);
    }

    @Override
    public int compareTo(TransactionId other) {
        // ignore the address when ordering transaction IDs
        return validStart.compareTo(other.validStart);
    }
}
