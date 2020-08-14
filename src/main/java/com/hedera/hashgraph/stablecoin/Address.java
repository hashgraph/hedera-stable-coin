package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PublicKey;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class Address {
    public static final Address ZERO = new Address(PublicKey.fromBytes(new byte[32]));

    public final PublicKey publicKey;

    public Address(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Address(@Nullable ByteString bytes) {
        this((bytes == null || bytes.isEmpty()) ? ZERO.publicKey : PublicKey.fromBytes(bytes.toByteArray()));
    }

    public boolean isZero() {
        return this.equals(ZERO) || Arrays.equals(publicKey.toBytes(), ZERO.publicKey.toBytes());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Address)) {
            return false;
        }

        return this.publicKey.equals(((Address) o).publicKey);
    }

    @Override
    public int hashCode() {
        return publicKey.hashCode();
    }
}
