package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ProposeOwnerTransactionArguments {
    public final Address address;

    public ProposeOwnerTransactionArguments(TransactionBody body) {
        assert body.hasProposeOwner();
        var data = body.getProposeOwner();

        address = new Address(data.getAddress());
    }
}
