package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ApproveAllowanceTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ApproveAllowanceTransaction extends Transaction {
    public ApproveAllowanceTransaction(
        Ed25519PrivateKey caller,
        Address spender,
        BigInteger value
    ) {
        super(caller, TransactionBody.newBuilder()
            .setApprove(ApproveAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(spender.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(value.toByteArray()))));
    }
}
