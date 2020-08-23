package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransferFromTransactionData;

import java.math.BigInteger;

public final class TransferFromTransaction extends Transaction {
    public TransferFromTransaction(
        PrivateKey caller,
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
