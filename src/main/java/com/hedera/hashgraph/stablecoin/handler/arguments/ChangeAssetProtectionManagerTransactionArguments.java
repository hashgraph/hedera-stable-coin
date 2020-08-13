package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ChangeAssetProtectionManagerTransactionArguments {
    public final Address address;

    public ChangeAssetProtectionManagerTransactionArguments(TransactionBody body) {
        var data = body.getChangeAssetProtectionManager();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
