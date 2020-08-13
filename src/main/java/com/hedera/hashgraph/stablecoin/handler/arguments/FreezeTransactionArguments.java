package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class FreezeTransactionArguments {
    public final Address address;

    public FreezeTransactionArguments(TransactionBody body) {
        var data = body.getFreeze();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
