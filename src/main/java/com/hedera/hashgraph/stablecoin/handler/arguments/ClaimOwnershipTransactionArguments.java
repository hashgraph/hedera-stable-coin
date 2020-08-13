package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ClaimOwnershipTransactionArguments {
    public final Address address;

    public ClaimOwnershipTransactionArguments(TransactionBody body) {
        var data = body.getClaimOwnership();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
