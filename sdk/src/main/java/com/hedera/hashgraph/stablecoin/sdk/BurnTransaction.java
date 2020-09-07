package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.BurnTransactionData;

import java.math.BigInteger;

public final class BurnTransaction extends Transaction {
    public BurnTransaction(
        Ed25519PrivateKey caller,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setBurn(BurnTransactionData.newBuilder()
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
