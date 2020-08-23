package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.UnfreezeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class UnfreezeTransactionHandler extends TransactionHandler<UnfreezeTransactionArguments> {
    @Override
    protected UnfreezeTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new UnfreezeTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, UnfreezeTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = assetProtectionManager || caller = owner
        ensureAuthorized(caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()));
    }

    @Override
    protected void updateState(State state, Address caller, UnfreezeTransactionArguments args) {
        // i. !Frozen[addr]
        state.unfreeze(args.address);
    }
}
