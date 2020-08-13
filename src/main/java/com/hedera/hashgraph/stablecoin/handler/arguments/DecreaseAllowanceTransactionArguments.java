package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class DecreaseAllowanceTransactionArguments {
    public final Address spender;

    public final BigInteger value;

    public DecreaseAllowanceTransactionArguments(TransactionBody body) {
        var data = body.getDecreaseAllowance();
        assert data != null;

        spender = new Address(data.getSpender());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
