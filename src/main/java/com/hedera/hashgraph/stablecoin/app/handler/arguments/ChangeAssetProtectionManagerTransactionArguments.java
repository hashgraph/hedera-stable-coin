package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeAssetProtectionManagerTransactionArguments {
    public final Address address;

    public ChangeAssetProtectionManagerTransactionArguments(TransactionBody body) {
        assert body.hasChangeAssetProtectionManager();
        var data = body.getChangeAssetProtectionManager();

        address = new Address(data.getAddress());
    }
}
