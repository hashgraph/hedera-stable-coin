package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.DecreaseAllowanceTransactionData;

import java.math.BigInteger;

public final class DecreaseAllowanceTransaction extends Transaction {
    public DecreaseAllowanceTransaction(
        PrivateKey caller,
        Address address,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setDecreaseAllowance(DecreaseAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(address.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
