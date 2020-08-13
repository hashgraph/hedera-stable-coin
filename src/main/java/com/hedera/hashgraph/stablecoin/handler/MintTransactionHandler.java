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
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.MINT_OWNER_NOT_SET);

        // ii. caller = SupplyManager || caller = Owner
        ensure(
            caller.equals(state.getSupplyManager()) || caller.equals(state.getOwner()),
            Status.MINT_NOT_AUTHORIZED
        );

        // iii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.MINT_VALUE_LESS_THAN_ZERO);

        // iv. TotalSupply + value <= MAX_INT // prevents overflow

        // v. TotalSupply >= Balances[SupplyManager]
        ensure(
            state.getTotalSupply().compareTo(state.getBalanceOf(state.getSupplyManager())) >= 0,
            Status.MINT_INSUFFICENT_TOTAL_SUPPLY
        );
    }

    @Override
    protected void updateState(State state, Address caller, MintTransactionArguments args) {
        state.increaseBalanceOf(state.getSupplyManager(), args.value);
    }
}
