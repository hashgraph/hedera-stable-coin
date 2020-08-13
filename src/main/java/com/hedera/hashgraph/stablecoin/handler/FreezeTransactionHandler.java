package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.FreezeTransactionArguments;

public final class FreezeTransactionHandler extends TransactionHandler<FreezeTransactionArguments> {
    @Override
    protected FreezeTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new FreezeTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, FreezeTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.FREEZE_OWNER_NOT_SET);

        // ii. caller = assetProtectionManager || caller = owner
        ensure(
            caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()),
            Status.FREEZE_NOT_AUTHORIZED
        );

        // iii. !isPrivilegedRole(addr)
        ensure(state.isPrivilegedRole(args.address), Status.FREEZE_ADDRESS_IS_PRIVILEGED);
    }

    @Override
    protected void updateState(State state, Address caller, FreezeTransactionArguments args) {
        // i. Frozen[addr] // upon completion, Frozen[addr] must be true
        state.setFreeze(args.address, true);
    }
}
