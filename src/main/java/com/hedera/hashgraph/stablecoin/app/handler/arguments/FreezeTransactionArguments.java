package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class FreezeTransactionArguments {
    public final Address address;

    public FreezeTransactionArguments(TransactionBody body) {
        assert body.hasFreeze();
        var data = body.getFreeze();

        address = new Address(data.getAddress());
    }
}
