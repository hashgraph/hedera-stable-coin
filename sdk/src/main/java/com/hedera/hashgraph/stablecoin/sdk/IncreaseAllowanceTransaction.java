package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.IncreaseAllowanceTransactionData;

import java.math.BigInteger;

public final class IncreaseAllowanceTransaction extends Transaction {
    public IncreaseAllowanceTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey caller,
        Address address,
        BigInteger amount
    ) {
        super(operatorAccountNum, caller, TransactionBody.newBuilder()
            .setIncreaseAllowance(IncreaseAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(address.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
