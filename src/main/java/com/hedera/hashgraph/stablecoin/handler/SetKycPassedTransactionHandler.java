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
        ensureOwnerSet(state);

        // ii. caller = assetProtectionManager || caller = owner
        ensureAssetProtectionManager(state, caller);
    }

    @Override
    protected void updateState(State state, Address caller, SetKycPassedTransactionArguments args) {
        // i. KycPassed[addr]
        state.setKycPassed(args.address);
    }
}
