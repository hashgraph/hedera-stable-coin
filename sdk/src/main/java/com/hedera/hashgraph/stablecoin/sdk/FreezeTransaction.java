package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.FreezeTransactionData;

public final class FreezeTransaction extends Transaction {
    public FreezeTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey owner,
        Address address
    ) {
        super(operatorAccountNum, owner, TransactionBody.newBuilder()
            .setFreeze(FreezeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
