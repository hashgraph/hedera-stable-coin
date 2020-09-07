package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.DecreaseAllowanceTransactionData;

import java.math.BigInteger;

public final class DecreaseAllowanceTransaction extends Transaction {
    public DecreaseAllowanceTransaction(
        Ed25519PrivateKey caller,
        Address address,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setDecreaseAllowance(DecreaseAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(address.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
