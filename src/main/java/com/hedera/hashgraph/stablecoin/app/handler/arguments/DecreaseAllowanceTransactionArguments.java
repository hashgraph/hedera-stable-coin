package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class DecreaseAllowanceTransactionArguments {
    public final Address spender;

    public final BigInteger value;

    public DecreaseAllowanceTransactionArguments(TransactionBody body) {
        assert body.hasDecreaseAllowance();
        var data = body.getDecreaseAllowance();

        spender = new Address(data.getSpender());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
