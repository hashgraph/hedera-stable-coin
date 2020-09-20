package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class MintTransactionArguments {
    public final BigInteger value;

    public MintTransactionArguments(TransactionBody body) {
        var data = body.getMint();

        value = new BigInteger(data.getValue().toByteArray());
    }
}
