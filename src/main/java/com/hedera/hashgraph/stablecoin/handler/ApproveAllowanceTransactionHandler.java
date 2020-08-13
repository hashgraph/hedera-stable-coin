package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.ApproveAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ApproveAllowanceTransactionHandler extends TransactionHandler<ApproveAllowanceTransactionArguments> {
    @Override
    protected ApproveAllowanceTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ApproveAllowanceTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ApproveAllowanceTransactionArguments args) {
        // i. Owner != 0x
        ensure(!state.hasOwner(), Status.APPROVE_ALLOWANCE_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.APPROVE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED);

        // iv. CheckTransferAllowed(spender)
        ensure(state.checkTransferAllowed(args.spender), Status.APPROVE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ApproveAllowanceTransactionArguments args) {
        // i. Allowances[caller][spender] = value
        state.setAllowance(caller, args.spender, args.value);
    }
}
