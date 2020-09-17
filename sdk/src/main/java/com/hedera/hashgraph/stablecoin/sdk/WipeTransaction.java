package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.proto.WipeTransactionData;

import java.math.BigInteger;

public final class WipeTransaction extends Transaction {
    public WipeTransaction(
        long operatorAccountNum,
        Ed25519PrivateKey owner,
        Address address,
        BigInteger value
    ) {
        super(operatorAccountNum, owner, TransactionBody.newBuilder()
            .setWipe(WipeTransactionData.newBuilder()
                .setAddress(ByteString.copyFrom(address.publicKey.toBytes()))
                .setValue(ByteString.copyFrom(value.toByteArray()))));
    }
}
