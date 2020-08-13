package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransactionArguments {
    public final Address address;

    public ChangeSupplyManagerTransactionArguments(TransactionBody body) {
        var data = body.getChangeSupplyManager();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
