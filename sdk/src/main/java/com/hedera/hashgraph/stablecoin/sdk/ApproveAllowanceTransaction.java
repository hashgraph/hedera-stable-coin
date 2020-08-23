package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ApproveAllowanceTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ApproveAllowanceTransaction extends Transaction {
    public ApproveAllowanceTransaction(
        PrivateKey caller,
        Address spender,
        BigInteger value
    ) {
        super(caller, TransactionBody.newBuilder()
            .setApprove(ApproveAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(spender.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(value.toByteArray()))));
    }
}
