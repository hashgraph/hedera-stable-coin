package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ChangeComplianceManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeComplianceManagerTransaction extends Transaction {
    public ChangeComplianceManagerTransaction(
        Ed25519PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeComplianceManager(ChangeComplianceManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
