package com.hedera.hashgraph.stablecoin.handler.arguments;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ConstructTransactionArguments {
    public final String tokenName;

    public final String tokenSymbol;

    public final BigInteger tokenDecimal;

    public final BigInteger totalSupply;

    public final Address supplyManager;

    public final Address assetProtectionManager;

    public ConstructTransactionArguments(TransactionBody body) {
        assert body.hasConstruct();
        var data = body.getConstruct();

        tokenName = data.getTokenName();
        tokenSymbol = data.getTokenSymbol();
        tokenDecimal = new BigInteger(data.getTokenDecimal().toByteArray());
        totalSupply = new BigInteger(data.getTotalSupply().toByteArray());
        supplyManager = new Address(data.getSupplyManager());
        assetProtectionManager = new Address(data.getAssetProtectionManager());
    }
}
