package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ClaimOwnershipTransactionArguments {
    public ClaimOwnershipTransactionArguments(TransactionBody body) {
        assert body.hasClaimOwnership();
    }
}
