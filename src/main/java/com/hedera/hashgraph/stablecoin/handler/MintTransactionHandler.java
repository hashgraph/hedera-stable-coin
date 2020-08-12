package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.MintTransactionArguments;

import java.math.BigInteger;

public final class MintTransactionHandler extends TransactionHandler<MintTransactionArguments> {
    @Override
    protected MintTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new MintTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, MintTransactionArguments args) {
        // todo: pre-conditions
    }

    @Override
    protected void validatePost(State state, Address caller, MintTransactionArguments args) {
        // fixme: not sure how to solve this? maybe save the previous balance as an instance variable?

        // todo: i. Balances[caller]' = Balances[caller] - value

        // todo: ii. Balances[to]' = Balances[to] + value
    }

    @Override
    protected void updateState(State state, Address caller, MintTransactionArguments args) {
        state.increaseBalanceOf(state.getSupplyManager(), args.value);
    }
}
