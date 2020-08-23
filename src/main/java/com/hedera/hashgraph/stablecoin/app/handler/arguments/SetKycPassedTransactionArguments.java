package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class SetKycPassedTransactionArguments {
    public final Address address;

    public SetKycPassedTransactionArguments(TransactionBody body) {
        assert body.hasSetKycPassed();
        var data = body.getSetKycPassed();

        address = new Address(data.getAddress());
    }
}
