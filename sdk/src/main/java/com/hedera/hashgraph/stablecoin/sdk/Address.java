package com.hedera.hashgraph.stablecoin.sdk;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class Address {
    public static final Address ZERO = new Address(Ed25519PublicKey.fromBytes(new byte[32]));

    public final Ed25519PublicKey publicKey;

    public Address(Ed25519PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Address(Ed25519PrivateKey privateKey) {
        this(privateKey.publicKey);
    }

    public Address(@Nullable ByteString bytes) {
        this((bytes == null || bytes.isEmpty()) ? ZERO.publicKey : Ed25519PublicKey.fromBytes(bytes.toByteArray()));
    }

    public static Address fromString(String s) {
        return new Address(Ed25519PublicKey.fromString(s));
    }

    public boolean isZero() {
        return this.equals(ZERO) || Arrays.equals(publicKey.toBytes(), ZERO.publicKey.toBytes());
    }

    public byte[] toBytes() {
        return publicKey.toBytes();
    }

    @Override
    public String toString() {
        // encode just the key (no alg prefix) for the address
        return BaseEncoding.base16().lowerCase().encode(publicKey.toBytes());
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
