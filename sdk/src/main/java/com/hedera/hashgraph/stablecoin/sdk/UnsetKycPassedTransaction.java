package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.UnsetKycPassedTransactionData;

public final class UnsetKycPassedTransaction extends Transaction {
    public UnsetKycPassedTransaction(
        PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setUnsetKycPassed(UnsetKycPassedTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
