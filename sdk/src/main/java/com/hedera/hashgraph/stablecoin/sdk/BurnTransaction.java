package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.BurnTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class BurnTransaction extends Transaction {
    public BurnTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey caller,
        BigInteger amount
    ) {
        super(operatorAccountNum, caller, TransactionBody.newBuilder()
            .setBurn(BurnTransactionData.newBuilder()
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
