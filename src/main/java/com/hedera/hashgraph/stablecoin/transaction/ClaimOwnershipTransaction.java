package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.ClaimOwnershipTransactionData;

public final class ClaimOwnershipTransaction extends Transaction {
    public ClaimOwnershipTransaction(
        PrivateKey claimer,
        Address address
    ) {
        super(claimer, TransactionBody.newBuilder()
            .setClaimOwnership(ClaimOwnershipTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
