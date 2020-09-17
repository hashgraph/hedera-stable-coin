package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.SetKycPassedTransactionData;

public final class SetKycPassedTransaction extends Transaction {
    public SetKycPassedTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey owner,
        Address address
    ) {
        super(operatorAccountNum, owner, TransactionBody.newBuilder()
            .setSetKycPassed(SetKycPassedTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
