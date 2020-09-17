package com.hedera.hashgraph.stablecoin.sdk;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ExternalTransferTransaction extends Transaction {
    public ExternalTransferTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey caller,
        Address from, // within *this* network
        String networkURI, // pointer to the network
        byte[] to, // within the external network
        BigInteger amount // how much is being transferred
    ) {
        super(operatorAccountNum, caller, TransactionBody.newBuilder()
            .setExternalTransfer(ExternalTransferTransactionHelper.create(from.publicKey.toBytes(), networkURI, to, amount)));
    }
}
