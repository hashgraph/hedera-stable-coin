package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.MintTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class MintTransactionHandler extends TransactionHandler<MintTransactionArguments> {
    @Override
    protected MintTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new MintTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, MintTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = SupplyManager || caller = Owner
        ensureSupplyManager(state, caller);

        // iii. value >= 0
        ensureZeroOrGreater(args.value, Status.MINT_VALUE_LESS_THAN_ZERO);

        // iv. TotalSupply + value <= MAX_INT (prevents overflow)
        // NOTE: overflow is not possible

        // v. TotalSupply >= Balances[SupplyManager]
        ensureEqualOrGreater(
            state.getTotalSupply(),
            state.getBalanceOf(state.getSupplyManager()),
            Status.MINT_INSUFFICIENT_TOTAL_SUPPLY
        );
    }

    @Override
    protected void updateState(State state, Address caller, MintTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply + value
        state.increaseTotalSupply(args.value);

        // ii. Balances[SupplyManager]’ = Balances[SupplyManager] + value
        state.increaseBalanceOf(state.getSupplyManager(), args.value);
    }
}
