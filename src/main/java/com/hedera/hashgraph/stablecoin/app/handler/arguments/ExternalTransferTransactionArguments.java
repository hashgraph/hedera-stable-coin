package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.math.BigInteger;

public class ExternalTransferTransactionArguments {
    public final Address from;

    public final String networkURI;

    public final ByteString to;

    public final BigInteger amount;

    public ExternalTransferTransactionArguments(TransactionBody body) {
        assert body.hasExternalTransfer();
        var data = body.getExternalTransfer();

        from = new Address(data.getFrom());
        networkURI = data.getNetworkURI();
        to = data.getTo();
        amount = new BigInteger(data.getAmount().toByteArray());
    }
}
