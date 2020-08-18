package com.hedera.hashgraph.stablecoin.transaction;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.ClaimOwnershipTransactionData;

public final class ClaimOwnershipTransaction extends Transaction {
    public ClaimOwnershipTransaction(
        PrivateKey claimer
    ) {
        super(claimer, TransactionBody.newBuilder()
            .setClaimOwnership(ClaimOwnershipTransactionData.newBuilder()));
    }
}
