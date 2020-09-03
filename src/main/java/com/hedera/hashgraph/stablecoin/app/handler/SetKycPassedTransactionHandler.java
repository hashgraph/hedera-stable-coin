package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.SetKycPassedTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class SetKycPassedTransactionHandler extends TransactionHandler<SetKycPassedTransactionArguments> {
    @Override
    public SetKycPassedTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new SetKycPassedTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, SetKycPassedTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = ComplianceManager || caller = Owner
        ensureComplianceManagerOrOwner(state, caller);
    }

    @Override
    protected void updateState(State state, Address caller, SetKycPassedTransactionArguments args) {
        // i. KycPassed[addr]
        state.setKycPassed(args.address);
    }
}
