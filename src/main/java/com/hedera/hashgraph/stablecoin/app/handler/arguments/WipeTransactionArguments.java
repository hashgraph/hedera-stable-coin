package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class WipeTransactionArguments {
    public final Address address;

    public WipeTransactionArguments(TransactionBody body) {
        assert body.hasWipe();
        var data = body.getWipe();

        address = new Address(data.getAddress());
    }
}

