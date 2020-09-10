package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public final class ApproveExternalTransferTransactionHandler extends TransactionHandler<ApproveExternalTransferTransactionArguments> {
    @Override
    public ApproveExternalTransferTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ApproveExternalTransferTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ApproveExternalTransferTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. amount >= 0
        ensureZeroOrGreater(args.amount, Status.APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // iv. value <= MAX_INT
        ensureLessThanMaxInt(args.amount, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
    }

    @Override
    protected void updateState(State state, Address caller, ApproveExternalTransferTransactionArguments args) {
        // i. Allowances[caller][spender] = value
        state.setExternalAllowance(caller, args.networkURI, args.to.toByteArray(), args.amount);
    }
}
