package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ClaimOwnershipTransactionArguments {
    public final Address address;

    public ClaimOwnershipTransactionArguments(TransactionBody body) {
        assert body.hasClaimOwnership();
        var data = body.getClaimOwnership();

        address = new Address(data.getAddress());
    }
}
