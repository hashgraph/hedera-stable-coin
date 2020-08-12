package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class TransferTransactionArguments {
    public final Address to;

    public final BigInteger value;

    public TransferTransactionArguments(TransactionBody body) {
        var data = body.getTransfer();
        assert data != null;

        to = new Address(data.getTo());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
