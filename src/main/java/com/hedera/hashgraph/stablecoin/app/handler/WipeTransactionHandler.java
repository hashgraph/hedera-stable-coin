package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.WipeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class WipeTransactionHandler extends TransactionHandler<WipeTransactionArguments> {
    @Override
    public WipeTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new WipeTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, WipeTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.INCREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. caller = EnforcementManager || caller = Owner
        ensureEnforcementManagerOrOwner(state, caller);

        // iv. value <= Balances[addr]
        ensure(args.value.compareTo(state.getBalanceOf(args.address)) <= 0, Status.WIPE_VALUE_WOULD_RESULT_IN_NEGATIVE_BALANCE);

        // v. Balances[addr] <= TotalSupply
        ensure(state.getBalanceOf(args.address).compareTo(state.getTotalSupply()) <= 0, Status.WIPE_VALUE_INSUFFICIENT_TOTAL_SUPPLY);

        // vi. value <= MAX_INT
        ensureLessThanMaxInt(args.value, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
    }

    @Override
    protected void updateState(State state, Address caller, WipeTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply - value // total supply decreased
        state.decreaseTotalSupply(args.value);

        // ii. Balances[addr]’ = Balances[addr] - value // balance “updated”
        state.decreaseBalanceOf(args.address, args.value);
    }
}
