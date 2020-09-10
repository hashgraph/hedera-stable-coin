package com.hedera.hashgraph.stablecoin.app;

import com.google.common.base.MoreObjects;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import java.util.Arrays;
import java.util.Objects;

public final class Tuple3 {
    public final Ed25519PublicKey first;

    public final String second;

    public final byte[] third;

    public Tuple3(Ed25519PublicKey first, String second, byte[] third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, Arrays.hashCode(third));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple3 tuple3 = (Tuple3) o;

        return first.equals(tuple3.first) &&
            second.equals(tuple3.second) &&
            Arrays.equals(third, tuple3.third);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .addValue(first)
            .addValue(second)
            .addValue(third)
            .toString();
    }
}
