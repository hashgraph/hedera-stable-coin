package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class BurnTransactionArguments {
    public final BigInteger value;

    public BurnTransactionArguments(TransactionBody body) {
        var data = body.getBurn();

        value = new BigInteger(data.getValue().toByteArray());
    }
}
