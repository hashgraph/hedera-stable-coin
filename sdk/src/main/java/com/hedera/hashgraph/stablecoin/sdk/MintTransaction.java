package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.MintTransactionData;

import java.math.BigInteger;

public final class MintTransaction extends Transaction {
    public MintTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey supplyManager,
        BigInteger amount
    ) {
        super(operatorAccountNum, supplyManager, TransactionBody.newBuilder()
            .setMint(MintTransactionData.newBuilder()
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
