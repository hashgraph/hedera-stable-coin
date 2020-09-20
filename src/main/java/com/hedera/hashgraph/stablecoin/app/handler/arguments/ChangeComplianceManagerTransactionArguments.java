package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeComplianceManagerTransactionArguments {
    public final Address address;

    public ChangeComplianceManagerTransactionArguments(TransactionBody body) {
        var data = body.getChangeComplianceManager();

        address = new Address(data.getAddress());
    }
}
