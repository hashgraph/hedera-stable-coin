package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.TransferTransactionData;

import java.math.BigInteger;

public final class TransferTransaction extends Transaction {
    public TransferTransaction(
        PrivateKey transferFrom,
        Address transferTo,
        BigInteger amount
    ) {
        super(transferFrom, TransactionBody.newBuilder()
            .setTransfer(TransferTransactionData.newBuilder()
                .setTo(ByteString.copyFrom(transferTo.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
