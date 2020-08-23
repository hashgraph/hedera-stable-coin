package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class UnsetKycPassedTransactionArguments {
    public final Address address;

    public UnsetKycPassedTransactionArguments(TransactionBody body) {
        assert body.hasUnsetKycPassed();
        var data = body.getUnsetKycPassed();

        address = new Address(data.getAddress());
    }
}
