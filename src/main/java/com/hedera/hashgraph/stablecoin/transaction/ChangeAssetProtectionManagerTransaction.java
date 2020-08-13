package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.ChangeAssetProtectionManagerTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeAssetProtectionManagerTransaction extends Transaction {
    public ChangeAssetProtectionManagerTransaction(
        PrivateKey caller,
        Address address
    ) {
        super(caller, TransactionBody.newBuilder()
            .setChangeAssetProtectionManager(ChangeAssetProtectionManagerTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
