package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class WipeTransactionArguments {
    public final Address address;

    public final BigInteger balance;

    public WipeTransactionArguments(TransactionBody body) {
        var data = body.getWipe();
        assert data != null;

        address = new Address(data.getAddress());
        balance = new BigInteger(data.getBalance().toByteArray());
    }
}

