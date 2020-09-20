package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.math.BigInteger;
import java.util.Arrays;

public class ApproveExternalTransferTransactionArguments {
    public final String networkURI;

    public final ByteString to;

    public final BigInteger amount;

    public ApproveExternalTransferTransactionArguments(TransactionBody body) {
        var data = body.getApproveExternalTransfer();

        networkURI = data.getNetworkURI();
        to = data.getTo();
        amount = new BigInteger(data.getAmount().toByteArray());
    }
}
