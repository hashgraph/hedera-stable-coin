package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.math.BigInteger;

public class ExternalTransferFromTransactionArguments {
    public final ByteString from;

    public final String networkURI;

    public final Address to;

    public final BigInteger amount;

    public ExternalTransferFromTransactionArguments(TransactionBody body) {
        var data = body.getExternalTransferFrom();

        from = data.getFrom();
        networkURI = data.getNetworkURI();
        to = new Address(data.getTo());
        amount = new BigInteger(data.getAmount().toByteArray());
    }
}
