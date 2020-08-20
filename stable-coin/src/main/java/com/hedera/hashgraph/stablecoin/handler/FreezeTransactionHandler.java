package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.FreezeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class FreezeTransactionHandler extends TransactionHandler<FreezeTransactionArguments> {
    @Override
    protected FreezeTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new FreezeTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, FreezeTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = assetProtectionManager || caller = owner
        ensureAssetProtectionManager(state, caller);

        // iii. !isPrivilegedRole(addr)
        ensure(!state.isPrivilegedRole(args.address), Status.FREEZE_ADDRESS_IS_PRIVILEGED);
    }

    @Override
    protected void updateState(State state, Address caller, FreezeTransactionArguments args) {
        // i. Frozen[addr] (upon completion, Frozen[addr] must be true)
        state.freeze(args.address);
    }
}
