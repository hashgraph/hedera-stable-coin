package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransferTransactionData;

import java.math.BigInteger;

public final class TransferTransaction extends Transaction {
    public TransferTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey transferFrom,
        Address transferTo,
        BigInteger amount
    ) {
        super(operatorAccountNum, transferFrom, TransactionBody.newBuilder()
            .setTransfer(TransferTransactionData.newBuilder()
                .setTo(ByteString.copyFrom(transferTo.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
