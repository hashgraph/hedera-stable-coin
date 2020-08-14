package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.IncreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class IncreaseAllowanceTransactionHandler extends TransactionHandler<IncreaseAllowanceTransactionArguments> {
    @Override
    protected IncreaseAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new IncreaseAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, IncreaseAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.INCREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // iv. CheckTransferAllowed(spender)
        ensureTransferAllowed(state, args.spender, Status.INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);

        // v. Allowances[caller][spender] + value <= MAX_INT
        // NOTE: not possible to overflow
    }

    @Override
    protected void updateState(State state, Address caller, IncreaseAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender]’ = Allowances[caller][spender] + value
        state.increaseAllowanceOf(caller, args.spender, args.value);
    }
}
