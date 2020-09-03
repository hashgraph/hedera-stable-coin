package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ChangeEnforcementManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public class ChangeEnforcementManagerTransaction extends Transaction {
    public ChangeEnforcementManagerTransaction(
        PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeEnforcementManager(ChangeEnforcementManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
