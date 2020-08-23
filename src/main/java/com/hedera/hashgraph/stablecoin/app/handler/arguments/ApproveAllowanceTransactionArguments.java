package com.hedera.hashgraph.stablecoin.app.handler.arguments;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public class ApproveAllowanceTransactionArguments {
    public final Address spender;
    public final BigInteger value;

    public ApproveAllowanceTransactionArguments(TransactionBody body) {
        assert body.hasApprove();
        var data = body.getApprove();

        spender = new Address(data.getSpender());
        value = new BigInteger(data.getValue().toByteArray());
    }
}
