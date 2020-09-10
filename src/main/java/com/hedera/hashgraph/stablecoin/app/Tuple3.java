package com.hedera.hashgraph.stablecoin.app;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class Tuple3<T1, T2, T3> {
    public final T1 first;

    public final T2 second;

    public final T3 third;

    public Tuple3(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;

        return first.equals(tuple3.first) &&
            second.equals(tuple3.second) &&
            third.equals(tuple3.third);
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
