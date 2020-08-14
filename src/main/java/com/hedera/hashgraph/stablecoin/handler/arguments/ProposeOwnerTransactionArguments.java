package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ProposeOwnerTransactionArguments {
    public final Address address;

    public ProposeOwnerTransactionArguments(TransactionBody body) {
        assert body.hasProposeOwner();
        var data = body.getProposeOwner();

        address = new Address(data.getAddress());
    }
}
