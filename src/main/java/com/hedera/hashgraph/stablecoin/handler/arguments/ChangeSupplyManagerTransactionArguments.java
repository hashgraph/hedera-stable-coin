package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransactionArguments {
    public final Address address;

    public ChangeSupplyManagerTransactionArguments(TransactionBody body) {
        assert body.hasChangeSupplyManager();
        var data = body.getChangeSupplyManager();

        address = new Address(data.getAddress());
    }
}
