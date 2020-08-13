package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class UnfreezeTransactionArguments {
    public final Address address;

    public UnfreezeTransactionArguments(TransactionBody body) {
        var data = body.getUnfreeze();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
