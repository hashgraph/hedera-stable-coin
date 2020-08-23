package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.UnsetKycPassedTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class UnsetKycPassedTransactionHandler extends TransactionHandler<UnsetKycPassedTransactionArguments> {
    @Override
    protected UnsetKycPassedTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new UnsetKycPassedTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, UnsetKycPassedTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = assetProtectionManager || caller = owner
        ensureAssetProtectionManager(state, caller);

        // iii. !isPrivilegedRole(addr)
        ensure(!state.isPrivilegedRole(args.address), Status.UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED);
    }

    @Override
    protected void updateState(State state, Address caller, UnsetKycPassedTransactionArguments args) {
        // i. !KycPassed[addr]
        state.unsetKycPassed(args.address);
    }
}
