package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.MintTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class MintTransactionHandler extends TransactionHandler<MintTransactionArguments> {
    @Override
    public MintTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new MintTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, MintTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = SupplyManager || caller = Owner
        ensureSupplyManagerOrOwner(state, caller);

        // iii. value >= 0
        ensureZeroOrGreater(args.value, Status.MINT_VALUE_LESS_THAN_ZERO);

        // iv. TotalSupply + value <= MAX_INT
        ensureLessThanMaxInt(state.getTotalSupply().add(args.value), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // v. TotalSupply >= Balances[SupplyManager]
        ensureEqualOrGreater(
            state.getTotalSupply(),
            state.getBalanceOf(state.getSupplyManager()),
            Status.MINT_INSUFFICIENT_TOTAL_SUPPLY
        );

        // vi. TotalSupply + value <= MAX_INT
        ensureLessThanMaxInt(state.getTotalSupply().add(args.value), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // vii. Balances[SupplyManager] + value <= MAX_INT
        ensureLessThanMaxInt(state.getBalanceOf(state.getSupplyManager()).add(args.value), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
    }

    @Override
    protected void updateState(State state, Address caller, MintTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply + value
        state.increaseTotalSupply(args.value);

        // ii. Balances[SupplyManager]’ = Balances[SupplyManager] + value
        state.increaseBalanceOf(state.getSupplyManager(), args.value);
    }
}
