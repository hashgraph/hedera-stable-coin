package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class UnfreezeTransactionArguments {
    public final Address address;

    public UnfreezeTransactionArguments(TransactionBody body) {
        assert body.hasUnfreeze();
        var data = body.getUnfreeze();

        address = new Address(data.getAddress());
    }
}
