package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransferFromTransactionData;

import java.math.BigInteger;

public final class TransferFromTransaction extends Transaction {
    public TransferFromTransaction(
        Ed25519PrivateKey caller,
        Address transferFrom,
        Address transferTo,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setTransferFrom(TransferFromTransactionData.newBuilder()
                .setFrom(ByteString.copyFrom(transferFrom.publicKey.toBytes()))
                .setTo(ByteString.copyFrom(transferTo.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
