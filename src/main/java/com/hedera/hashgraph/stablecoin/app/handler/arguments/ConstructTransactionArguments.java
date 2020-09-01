package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ConstructTransactionArguments {
    public final String tokenName;

    public final String tokenSymbol;

    public final int tokenDecimal;

    public final BigInteger totalSupply;

    public final Address supplyManager;

    public final Address assetProtectionManager;

    public ConstructTransactionArguments(TransactionBody body) {
        assert body.hasConstruct();
        var data = body.getConstruct();

        tokenName = data.getTokenName();
        tokenSymbol = data.getTokenSymbol();
        tokenDecimal = data.getTokenDecimal();
        totalSupply = new BigInteger(data.getTotalSupply().toByteArray());
        supplyManager = new Address(data.getSupplyManager());
        assetProtectionManager = new Address(data.getAssetProtectionManager());
    }
}
