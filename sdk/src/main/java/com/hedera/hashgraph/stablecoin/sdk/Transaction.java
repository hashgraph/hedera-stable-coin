package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public abstract class Transaction {
    private final ByteString transactionBytes;

    Transaction(PrivateKey caller, TransactionBody.Builder transactionBodyBuilder) {
        transactionBodyBuilder.setCaller(ByteString.copyFrom(caller.getPublicKey().toBytes()));

        var transactionBodyBytes = transactionBodyBuilder.build().toByteArray();
        var signature = caller.sign(transactionBodyBytes);

        transactionBytes = com.hedera.hashgraph.stablecoin.proto.Transaction.newBuilder()
            .setSignature(ByteString.copyFrom(signature))
            .setBody(ByteString.copyFrom(transactionBodyBytes))
            .build()
            .toByteString();
    }

    public byte[] toByteArray() {
        return transactionBytes.toByteArray();
    }
}
