package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.MintTransactionData;

import java.math.BigInteger;

public final class MintTransaction extends Transaction {
    public MintTransaction(
        PrivateKey supplyManager,
        BigInteger amount
    ) {
        super(supplyManager, TransactionBody.newBuilder()
            .setMint(MintTransactionData.newBuilder()
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
