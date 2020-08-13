package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.ChangeSupplyManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransaction extends Transaction {
    public ChangeSupplyManagerTransaction(
        PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeSupplyManager(ChangeSupplyManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
