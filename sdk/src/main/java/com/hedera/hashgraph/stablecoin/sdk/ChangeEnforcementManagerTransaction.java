package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ChangeEnforcementManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public class ChangeEnforcementManagerTransaction extends Transaction {
    public ChangeEnforcementManagerTransaction(
        Ed25519PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeEnforcementManager(ChangeEnforcementManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
