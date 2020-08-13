package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.BurnTransactionArguments;

import java.math.BigInteger;

public final class BurnTransactionHandler extends TransactionHandler<BurnTransactionArguments> {
    @Override
    protected BurnTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new BurnTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, BurnTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.BURN_OWNER_NOT_SET);

        // ii. caller = SupplyManager || caller = Owner
        ensure(
            caller.equals(state.getSupplyManager()) || caller.equals(state.getOwner()), 
            Status.BURN_NOT_AUTHORIZED
        );

        // iii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.BURN_VALUE_LESS_THAN_ZERO);

        // iv. Balances[SupplyManager] >= value
        ensure(
            state.getBalanceOf(state.getSupplyManager()).compareTo(args.value) >= 0,
            Status.BURN_INSUFFICENT_SUPPLY_MANAGER_BALANCE
        );

        // v. TotalSupply >= Balances[SupplyManager]
        ensure(
            state.getTotalSupply().compareTo(state.getBalanceOf(state.getSupplyManager())) >= 0, 
            Status.BURN_INSUFFICENT_TOTAL_SUPPLY
        );
    }

    @Override
    protected void updateState(State state, Address caller, BurnTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply - value // the new supply is decreased by value
        var totalSupplyPrime = state.getTotalSupply().subtract(args.value);

        // ii. Balances[SupplyManager]’ = Balances[SupplyManager] - value
        var supplyManagerBalancePrime = state.getBalanceOf(state.getSupplyManager()).subtract(args.value);

        state.setTotalSupply(totalSupplyPrime);
        state.setBalance(state.getSupplyManager(), supplyManagerBalancePrime);
    }
}
