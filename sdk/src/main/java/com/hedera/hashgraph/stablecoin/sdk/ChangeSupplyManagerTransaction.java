package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ChangeSupplyManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransaction extends Transaction {
    public ChangeSupplyManagerTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey caller,
        Address address
    ) {
        super(operatorAccountNum, caller, TransactionBody.newBuilder()
            .setChangeSupplyManager(ChangeSupplyManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
