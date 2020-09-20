package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class WipeTransactionArguments {
    public final Address address;

    public final BigInteger value;

    public WipeTransactionArguments(TransactionBody body) {
        var data = body.getWipe();

        address = new Address(data.getAddress());
        value = new BigInteger(data.getValue().toByteArray());
    }
}

