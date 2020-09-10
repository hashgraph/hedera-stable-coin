package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ApproveExternalTransferTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ApproveExternalTransferTransaction extends Transaction {
    public ApproveExternalTransferTransaction(
        Ed25519PrivateKey caller,
        String networkURI, // pointer to the network
        byte[] to, // within the external network
        BigInteger amount // how much is being transferred
    ) {
        super(caller, TransactionBody.newBuilder()
            .setApproveExternalTransfer(ApproveExternalTransferTransactionData.newBuilder()
                .setNetworkURI(networkURI)
                .setTo(ByteString.copyFrom(to))
                .setAmount(ByteString.copyFrom(amount.toByteArray()))
                .build()));
    }
}
