package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.DecreaseAllowanceTransactionArguments;

import java.math.BigInteger;

public final class DecreaseAllowanceTransactionHandler extends TransactionHandler<DecreaseAllowanceTransactionArguments> {
    @Override
    protected DecreaseAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new DecreaseAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, DecreaseAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.DECREASE_ALLOWANCE_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.DECREASE_ALLOWANCE_VALUE_IS_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.DECREASE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED);

        // iv. CheckTransferAllowed(spender)
        ensure(state.checkTransferAllowed(args.spender), Status.DECREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);

        // v. Allowances[caller][spender] >= value
        ensure(
            state.getAllowance(caller, args.spender).compareTo(args.value) <= 0,
            Status.DECREASE_ALLOWANCE_VALUE_EXCEEDS_ALLOWANCE
        );
    }

    @Override
    protected void updateState(State state, Address caller, DecreaseAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender]’ = Allowances[caller][spender] - value
        var allowancePrime = state.getAllowance(caller, args.spender).subtract(args.value);

        state.setAllowance(caller, args.spender, allowancePrime);
    }
}
