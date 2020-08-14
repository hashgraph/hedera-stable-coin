package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class MintTransactionArguments {
    public final BigInteger value;

    public MintTransactionArguments(TransactionBody body) {
        assert body.hasMint();
        var data = body.getMint();

        value = new BigInteger(data.getValue().toByteArray());
    }
}
