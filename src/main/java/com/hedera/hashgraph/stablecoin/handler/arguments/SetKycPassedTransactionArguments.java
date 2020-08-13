package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class SetKycPassedTransactionArguments {
    public final Address address;

    public SetKycPassedTransactionArguments(TransactionBody body) {
        var data = body.getSetKycPassed();
        assert data != null;

        address = new Address(data.getAddress());
    }
}
