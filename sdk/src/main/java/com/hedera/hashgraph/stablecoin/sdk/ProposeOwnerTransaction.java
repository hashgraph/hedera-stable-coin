package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.ProposeOwnerTransactionData;

public final class ProposeOwnerTransaction extends Transaction {
    public ProposeOwnerTransaction(
        Ed25519PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setProposeOwner(ProposeOwnerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
