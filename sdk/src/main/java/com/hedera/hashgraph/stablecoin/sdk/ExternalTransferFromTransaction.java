package com.hedera.hashgraph.stablecoin.sdk;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ExternalTransferFromTransaction extends Transaction {
    public ExternalTransferFromTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey caller,
        byte[] from, // within the external network
        String networkURI, // pointer to the network
        Address to, // within *this* network
        BigInteger amount // how much is being transferred
    ) {
        super(operatorAccountNum, caller, TransactionBody.newBuilder()
            .setExternalTransferFrom(ExternalTransferTransactionHelper.create(from, networkURI, to.publicKey.toBytes(), amount)));
    }
}
