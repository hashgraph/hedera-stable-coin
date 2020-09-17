package com.hedera.hashgraph.stablecoin.sdk;

import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.ClaimOwnershipTransactionData;

public final class ClaimOwnershipTransaction extends Transaction {
    public ClaimOwnershipTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey claimer
    ) {
        super(operatorAccountNum, claimer, TransactionBody.newBuilder()
            .setClaimOwnership(ClaimOwnershipTransactionData.newBuilder()));
    }
}
