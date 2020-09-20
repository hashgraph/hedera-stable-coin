package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ChangeEnforcementManagerTransactionArguments {
    public final Address address;

    public ChangeEnforcementManagerTransactionArguments(TransactionBody body) {
        var data = body.getChangeEnforcementManager();

        address = new Address(data.getAddress());
    }
}
