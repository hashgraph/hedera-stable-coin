package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.proto.ExternalTransferTransactionData;

import java.math.BigInteger;


public final class ExternalTransferTransactionHelper {
    private ExternalTransferTransactionHelper() {
    }

    static ExternalTransferTransactionData create(
        byte[] from, // within *this* network
        String networkURI, // pointer to the network
        byte[] to, // within the external network
        BigInteger amount // how much is being transferred
    ) {
        return ExternalTransferTransactionData.newBuilder()
            .setFrom(ByteString.copyFrom(from))
            .setNetworkURI(networkURI)
            .setTo(ByteString.copyFrom(to))
            .setAmount(ByteString.copyFrom(amount.toByteArray()))
            .build();
    }
}
