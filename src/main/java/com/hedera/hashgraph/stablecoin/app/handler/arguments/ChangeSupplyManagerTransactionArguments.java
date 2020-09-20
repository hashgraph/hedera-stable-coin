package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransactionArguments {
    public final Address address;

    public ChangeSupplyManagerTransactionArguments(TransactionBody body) {
        var data = body.getChangeSupplyManager();

        address = new Address(data.getAddress());
    }
}
