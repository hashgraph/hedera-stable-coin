package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.IncreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class IncreaseAllowanceTransactionHandler extends TransactionHandler<IncreaseAllowanceTransactionArguments> {
    @Override
    public IncreaseAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new IncreaseAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, IncreaseAllowanceTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.INCREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // iv. CheckTransferAllowed(spender)
        ensureTransferAllowed(state, args.spender, Status.INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);

        // v. Allowances[caller][spender] + value <= MAX_INT
        ensureLessThanMaxInt(state.getAllowance(caller, args.spender).add(args.value), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
    }

    @Override
    protected void updateState(State state, Address caller, IncreaseAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender]’ = Allowances[caller][spender] + value
        state.increaseAllowanceOf(caller, args.spender, args.value);
    }
}
