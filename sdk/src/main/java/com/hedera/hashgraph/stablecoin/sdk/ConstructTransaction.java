package com.hedera.hashgraph.stablecoin.sdk;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.ConstructTransactionData;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ConstructTransaction extends Transaction {
    public ConstructTransaction(
        PrivateKey owner,
        String tokenName,
        String tokenSymbol,
        int tokenDecimal,
        BigInteger totalSupply,
        Address supplyManager,
        Address complianceManager
    ) {
        super(owner, TransactionBody.newBuilder()
            .setConstruct(ConstructTransactionData.newBuilder()
                .setTokenSymbol(tokenSymbol)
                .setTokenName(tokenName)
                .setTokenDecimal(tokenDecimal)
                .setTotalSupply(ByteString.copyFrom(totalSupply.toByteArray()))
                .setSupplyManager(ByteString.copyFrom(supplyManager.publicKey.toBytes()))
                .setComplianceManager(ByteString.copyFrom(complianceManager.publicKey.toBytes()))
                .setEnforcementManager(ByteString.copyFrom(complianceManager.publicKey.toBytes()))));
    }
}
