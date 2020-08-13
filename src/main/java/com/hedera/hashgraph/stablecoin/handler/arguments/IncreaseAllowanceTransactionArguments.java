package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class IncreaseAllowanceTransactionArguments {
    public final Address spender;

    public final BigInteger value;

    public IncreaseAllowanceTransactionArguments(TransactionBody body) {
        var data = body.getIncreaseAllowance();
        assert data != null;

        spender = new Address(data.getSpender());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
