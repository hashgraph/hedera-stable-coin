package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.IncreaseAllowanceTransactionArguments;

import java.math.BigInteger;

public final class IncreaseAllowanceTransactionHandler extends TransactionHandler<IncreaseAllowanceTransactionArguments> {
    @Override
    protected IncreaseAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new IncreaseAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, IncreaseAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.INCREASE_ALLOWANCE_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.INCREASE_ALLOWANCE_VALUE_IS_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.INCREASE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED);

        // iv. CheckTransferAllowed(spender)
        ensure(state.checkTransferAllowed(args.spender), Status.INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);

        // v. Allowances[caller][spender] + value <= MAX_INT
    }

    @Override
    protected void updateState(State state, Address caller, IncreaseAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender]’ = Allowances[caller][spender] + value
        var allowancePrime = state.getAllowance(caller, args.spender).add(args.value);

        state.setAllowance(caller, args.spender, allowancePrime);
    }
}
