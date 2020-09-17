package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.UnfreezeTransactionData;

public final class UnfreezeTransaction extends Transaction {
    public UnfreezeTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey owner,
        Address address
    ) {
        super(operatorAccountNum, owner, TransactionBody.newBuilder()
            .setUnfreeze(UnfreezeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
