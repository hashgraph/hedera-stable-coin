package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class MintTransactionArguments {
    public final BigInteger value;

    public MintTransactionArguments(TransactionBody body) {
        var data = body.getMint();
        assert data != null;

        value = new BigInteger(data.getValue().toByteArray());
    }
}
