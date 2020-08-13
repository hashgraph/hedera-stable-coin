package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.UnfreezeTransactionData;

public final class UnfreezeTransaction extends Transaction {
    public UnfreezeTransaction(
        PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setUnfreeze(UnfreezeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
