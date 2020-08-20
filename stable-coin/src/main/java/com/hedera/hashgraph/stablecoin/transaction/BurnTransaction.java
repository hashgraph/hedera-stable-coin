package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.BurnTransactionData;

import java.math.BigInteger;

public final class BurnTransaction extends Transaction {
    public BurnTransaction(
        PrivateKey caller,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setBurn(BurnTransactionData.newBuilder()
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
