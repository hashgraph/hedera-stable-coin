package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class FreezeTransactionArguments {
    public final Address address;

    public FreezeTransactionArguments(TransactionBody body) {
        assert body.hasFreeze();
        var data = body.getFreeze();

        address = new Address(data.getAddress());
    }
}
