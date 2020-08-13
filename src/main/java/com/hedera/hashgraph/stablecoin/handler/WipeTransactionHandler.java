package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.WipeTransactionArguments;

import java.math.BigInteger;

public final class WipeTransactionHandler extends TransactionHandler<WipeTransactionArguments> {
    @Override
    protected WipeTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new WipeTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, WipeTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.WIPE_OWNER_NOT_SET);

        // ii. caller = assetProtectionManager || caller = owner
        ensure(
            caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()), 
            Status.WIPE_NOT_AUTHORIZED
        );

        
        // iii. Frozen[addr]
        ensure(state.isFrozen(args.address), Status.WIPE_ADDRESS_NOT_FROZEN);
    }

    @Override
    protected void updateState(State state, Address caller, WipeTransactionArguments args) {
        var balance = state.getBalanceOf(args.address);

        // TotalSupply’ = TotalSupply - Balances[addr] // total supply decreased
        var totalSupplyPrime = state.getTotalSupply().subtract(balance);

        // Balances[addr]’ = 0 // balance “updated” to 0
        var balancePrime = BigInteger.ZERO;

        state.setTotalSupply(totalSupplyPrime);
        state.setBalance(args.address, balancePrime);
    }
}
