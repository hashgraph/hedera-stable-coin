package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.FreezeTransactionData;

public final class FreezeTransaction extends Transaction {
    public FreezeTransaction(
        PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setFreeze(FreezeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
