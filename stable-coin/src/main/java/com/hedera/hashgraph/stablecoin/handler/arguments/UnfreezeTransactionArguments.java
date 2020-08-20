package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class UnfreezeTransactionArguments {
    public final Address address;

    public UnfreezeTransactionArguments(TransactionBody body) {
        assert body.hasUnfreeze();
        var data = body.getUnfreeze();

        address = new Address(data.getAddress());
    }
}
