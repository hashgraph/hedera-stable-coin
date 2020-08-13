package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.SetKycPassedTransactionArguments;

public final class SetKycPassedTransactionHandler extends TransactionHandler<SetKycPassedTransactionArguments> {
    @Override
    protected SetKycPassedTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new SetKycPassedTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, SetKycPassedTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.SET_KYC_PASSED_OWNER_NOT_SET);

        // ii. caller = assetProtectionManager || caller = owner
        ensure(
            caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()),
            Status.SET_KYC_PASSED_NOT_AUTHORIZED
        );
    }

    @Override
    protected void updateState(State state, Address caller, SetKycPassedTransactionArguments args) {
        // i. KycPassed[addr]
        state.setKycPassed(args.address, true);
    }
}
