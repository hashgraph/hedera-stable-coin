package com.hedera.hashgraph.stablecoin.sdk;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class Transaction {
    private static long lastValidStart = 0;

    public final TransactionId transactionId;

    private final ByteString transactionBytes;

    Transaction(long operatorAccountNum, Ed25519PrivateKey caller, TransactionBody.Builder transactionBodyBuilder) {
        var callerAddress = new Address(caller);
        var validStartNanos = getMonotonicNanoseconds();

        transactionBodyBuilder.setCaller(ByteString.copyFrom(callerAddress.toBytes()));
        transactionBodyBuilder.setOperatorAccountNum(operatorAccountNum);
        transactionBodyBuilder.setValidStartNanos(validStartNanos);

        var transactionBodyBytes = transactionBodyBuilder.build().toByteArray();
        var signature = caller.sign(transactionBodyBytes);

        this.transactionBytes = com.hedera.hashgraph.stablecoin.proto.Transaction.newBuilder()
            .setSignature(ByteString.copyFrom(signature))
            .setBody(ByteString.copyFrom(transactionBodyBytes))
            .build()
            .toByteString();

        this.transactionId = TransactionId.withValidStart(
            new AccountId(operatorAccountNum), Instant.ofEpochSecond(0, validStartNanos));
    }

    private static synchronized long getMonotonicNanoseconds() {
        var now = Instant.now().minus(10, ChronoUnit.SECONDS);

        @Var var validStart = ChronoUnit.NANOS.between(Instant.EPOCH, now);

        if (validStart <= lastValidStart) {
            validStart = lastValidStart + 1;
        }

        lastValidStart = validStart;

        return validStart;
    }

    public byte[] toByteArray() {
        return transactionBytes.toByteArray();
    }
}
