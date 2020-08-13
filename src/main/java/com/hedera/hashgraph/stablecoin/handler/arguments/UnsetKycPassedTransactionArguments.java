package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class UnsetKycPassedTransactionArguments {
    public final Address address;

    public UnsetKycPassedTransactionArguments(TransactionBody body) {
        var data = body.getUnsetKycPassed();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
