package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeAssetProtectionManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeAssetProtectionManagerTransactionHandler extends TransactionHandler<ChangeAssetProtectionManagerTransactionArguments> {
    @Override
    protected ChangeAssetProtectionManagerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ChangeAssetProtectionManagerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ChangeAssetProtectionManagerTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = Owner
        ensureAuthorized(caller.equals(state.getOwner()));

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.CHANGE_ASSET_PROTECTION_MANAGER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensureTransferAllowed(state, args.address, Status.CHANGE_ASSET_PROTECTION_MANAGER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ChangeAssetProtectionManagerTransactionArguments args) {
        // i. AssetProtectionManager = addr
        state.setAssetProtectionManager(args.address);
    }
}
