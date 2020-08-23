package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class IncreaseAllowanceTransactionArguments {
    public final Address spender;

    public final BigInteger value;

    public IncreaseAllowanceTransactionArguments(TransactionBody body) {
        assert body.hasIncreaseAllowance();
        var data = body.getIncreaseAllowance();

        spender = new Address(data.getSpender());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
