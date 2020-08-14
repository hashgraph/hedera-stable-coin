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
        ensureOwnerSet(state);

        // ii. caller = assetProtectionManager || caller = owner
        ensureAssetProtectionManager(state, caller);

        // iii. Frozen[addr]
        ensure(state.isFrozen(args.address), Status.WIPE_ADDRESS_NOT_FROZEN);
    }

    @Override
    protected void updateState(State state, Address caller, WipeTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply - Balances[addr] // total supply decreased
        state.decreaseTotalSupply(state.getBalanceOf(args.address));

        // ii. Balances[addr]’ = 0
        state.clearBalanceOf(args.address);
    }
}
