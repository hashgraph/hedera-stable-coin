package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ApproveAllowanceTransactionHandler extends TransactionHandler<ApproveAllowanceTransactionArguments> {
    @Override
    protected ApproveAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ApproveAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ApproveAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // iv. CheckTransferAllowed(spender)
        ensureTransferAllowed(state, args.spender, Status.APPROVE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ApproveAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender] = value
        state.setAllowance(caller, args.spender, args.value);
    }
}
