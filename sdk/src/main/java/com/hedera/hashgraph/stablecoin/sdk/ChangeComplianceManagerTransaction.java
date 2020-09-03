package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ChangeComplianceManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeComplianceManagerTransaction extends Transaction {
    public ChangeComplianceManagerTransaction(
        PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeComplianceManager(ChangeComplianceManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
