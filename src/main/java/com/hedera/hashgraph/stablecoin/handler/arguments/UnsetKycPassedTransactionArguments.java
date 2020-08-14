package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class UnsetKycPassedTransactionArguments {
    public final Address address;

    public UnsetKycPassedTransactionArguments(TransactionBody body) {
        assert body.hasUnsetKycPassed();
        var data = body.getUnsetKycPassed();

        address = new Address(data.getAddress());
    }
}
