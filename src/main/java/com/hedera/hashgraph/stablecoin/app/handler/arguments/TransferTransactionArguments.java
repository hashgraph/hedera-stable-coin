package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class TransferTransactionArguments {
    public final Address to;

    public final BigInteger value;

    public TransferTransactionArguments(TransactionBody body) {
        assert body.hasTransfer();
        var data = body.getTransfer();

        to = new Address(data.getTo());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
