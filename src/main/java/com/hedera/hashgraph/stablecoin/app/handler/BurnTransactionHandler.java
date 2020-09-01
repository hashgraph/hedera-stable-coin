package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.BurnTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class BurnTransactionHandler extends TransactionHandler<BurnTransactionArguments> {
    @Override
    public BurnTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new BurnTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, BurnTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = SupplyManager || caller = Owner
        ensureSupplyManagerOrOwner(state, caller);

        // iii. value >= 0
        ensureZeroOrGreater(args.value, Status.BURN_VALUE_LESS_THAN_ZERO);

        // iv. Balances[SupplyManager] >= value
        ensureEqualOrGreater(
            state.getBalanceOf(state.getSupplyManager()),
            args.value,
            Status.BURN_INSUFFICIENT_SUPPLY_MANAGER_BALANCE
        );

        // v. TotalSupply >= Balances[SupplyManager]
        ensureEqualOrGreater(
            state.getTotalSupply(),
            state.getBalanceOf(state.getSupplyManager()),
            Status.BURN_INSUFFICIENT_TOTAL_SUPPLY
        );
    }

    @Override
    protected void updateState(State state, Address caller, BurnTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply - value
        state.decreaseTotalSupply(args.value);

        // ii. Balances[SupplyManager]’ = Balances[SupplyManager] - value
        state.decreaseBalanceOf(state.getSupplyManager(), args.value);
    }
}
