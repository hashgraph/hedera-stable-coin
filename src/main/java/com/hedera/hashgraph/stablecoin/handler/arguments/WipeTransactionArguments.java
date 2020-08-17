package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class WipeTransactionArguments {
    public final Address address;

    public WipeTransactionArguments(TransactionBody body) {
        assert body.hasWipe();
        var data = body.getWipe();

        address = new Address(data.getAddress());
    }
}

