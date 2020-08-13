package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ProposeOwnerTransactionArguments {
    public final Address address;

    public ProposeOwnerTransactionArguments(TransactionBody body) {
        var data = body.getProposeOwner();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
