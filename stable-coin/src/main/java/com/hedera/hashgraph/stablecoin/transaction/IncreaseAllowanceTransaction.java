package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.IncreaseAllowanceTransactionData;

import java.math.BigInteger;

public final class IncreaseAllowanceTransaction extends Transaction {
    public IncreaseAllowanceTransaction(
        PrivateKey caller,
        Address address,
        BigInteger amount
    ) {
        super(caller, TransactionBody.newBuilder()
            .setIncreaseAllowance(IncreaseAllowanceTransactionData.newBuilder()
                .setSpender(ByteString.copyFrom(address.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(amount.toByteArray()))));
    }
}
