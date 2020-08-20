package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class TransferFromTransactionArguments {
    public final Address from;

    public final Address to;

    public final BigInteger value;

    public TransferFromTransactionArguments(TransactionBody body) {
        assert body.hasTransferFrom();
        var data = body.getTransferFrom();

        from = new Address(data.getFrom());
        to = new Address(data.getTo());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
