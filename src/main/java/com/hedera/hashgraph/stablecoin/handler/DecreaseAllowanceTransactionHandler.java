package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.DecreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class DecreaseAllowanceTransactionHandler extends TransactionHandler<DecreaseAllowanceTransactionArguments> {
    @Override
    protected DecreaseAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new DecreaseAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, DecreaseAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.DECREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // iv. CheckTransferAllowed(spender)
        ensureTransferAllowed(state, args.spender, Status.DECREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);

        // v. Allowances[caller][spender] >= value
        ensureEqualOrGreater(
            state.getAllowance(caller, args.spender),
            args.value,
            Status.DECREASE_ALLOWANCE_VALUE_EXCEEDS_ALLOWANCE
        );
    }

    @Override
    protected void updateState(State state, Address caller, DecreaseAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender]’ = Allowances[caller][spender] - value
        state.decreaseAllowanceOf(caller, args.spender, args.value);
    }
}
