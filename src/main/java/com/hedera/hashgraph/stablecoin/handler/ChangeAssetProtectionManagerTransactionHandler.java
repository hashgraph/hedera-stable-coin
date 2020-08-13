package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.ChangeAssetProtectionManagerTransactionArguments;

public final class ChangeAssetProtectionManagerTransactionHandler extends TransactionHandler<ChangeAssetProtectionManagerTransactionArguments> {
    @Override
    protected ChangeAssetProtectionManagerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ChangeAssetProtectionManagerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ChangeAssetProtectionManagerTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.CHANGE_ASSET_PROTECTION_MANAGER_OWNER_NOT_SET);

        // ii. caller = Owner
        ensure(caller.equals(state.getOwner()), Status.CHANGE_ASSET_PROTECTION_MANAGER_CALLER_NOT_OWNER);

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.CHANGE_ASSET_PROTECTION_MANAGER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensure(state.checkTransferAllowed(args.address), Status.CHANGE_ASSET_PROTECTION_MANAGER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ChangeAssetProtectionManagerTransactionArguments args) {
        // i. AssetProtectionManager = addr
        state.setAssetProtectionManager(args.address);
    }
}
