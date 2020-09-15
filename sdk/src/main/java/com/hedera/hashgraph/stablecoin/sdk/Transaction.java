package com.hedera.hashgraph.stablecoin.sdk;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public abstract class Transaction {
    private static long lastValidStart = 0;

    private final ByteString transactionBytes;

    public final TransactionId transactionId;

    Transaction(Ed25519PrivateKey caller, TransactionBody.Builder transactionBodyBuilder) {
        var callerAddress = new Address(caller);

        var transactionId = com.hedera.hashgraph.stablecoin.proto.TransactionId.newBuilder()
            .setAddress(ByteString.copyFrom(callerAddress.toBytes()))
            .setValidStart(getMonotonicNanoseconds())
            .build();

        transactionBodyBuilder.setTransactionId(transactionId);

        var transactionBodyBytes = transactionBodyBuilder.build().toByteArray();
        var signature = caller.sign(transactionBodyBytes);

        this.transactionBytes = com.hedera.hashgraph.stablecoin.proto.Transaction.newBuilder()
            .setSignature(ByteString.copyFrom(signature))
            .setBody(ByteString.copyFrom(transactionBodyBytes))
            .build()
            .toByteString();

        this.transactionId = new TransactionId(
            callerAddress, Instant.ofEpochSecond(0, transactionId.getValidStart()));
    }

    public byte[] toByteArray() {
        return transactionBytes.toByteArray();
    }

    private static synchronized long getMonotonicNanoseconds() {
        @Var var validStart = ChronoUnit.NANOS.between(Instant.EPOCH, Instant.now());

        if (lastValidStart <= validStart) {
            validStart = lastValidStart + 1;
        }

        lastValidStart = validStart;

        return validStart;
    }
}
