package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.ProposeOwnerTransactionData;

public final class ProposeOwnerTransaction extends Transaction {
    public ProposeOwnerTransaction(
        PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setProposeOwner(ProposeOwnerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
