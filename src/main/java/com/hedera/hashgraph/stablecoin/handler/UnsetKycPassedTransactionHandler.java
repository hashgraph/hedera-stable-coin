package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.UnsetKycPassedTransactionArguments;

public final class UnsetKycPassedTransactionHandler extends TransactionHandler<UnsetKycPassedTransactionArguments> {
    @Override
    protected UnsetKycPassedTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new UnsetKycPassedTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, UnsetKycPassedTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.UNSET_KYC_PASSED_OWNER_NOT_SET);

        // ii. caller = assetProtectionManager || caller = owner
        ensure(
            caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()),
            Status.UNSET_KYC_PASSED_NOT_AUTHORIZED
        );

        // iii. !isPrivilegedRole(addr)
        ensure(!state.isPrivilegedRole(args.address), Status.UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED);
    }

    @Override
    protected void updateState(State state, Address caller, UnsetKycPassedTransactionArguments args) {
        // i. !KycPassed[addr]
        state.setKycPassed(args.address, false);
    }
}
