package com.hedera.hashgraph.stablecoin.transaction;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.WipeTransactionData;

public final class WipeTransaction extends Transaction {
    public WipeTransaction(
        PrivateKey owner,
        Address address
    ) {
        super(owner, TransactionBody.newBuilder()
            .setWipe(WipeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))));
    }
}
