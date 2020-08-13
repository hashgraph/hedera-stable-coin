package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.ApproveTransactionArguments;
import com.hedera.hashgraph.stablecoin.handler.arguments.ConstructTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

import java.math.BigInteger;

public final class ApproveTransactionHandler extends TransactionHandler<ApproveTransactionArguments> {
    @Override
    protected ApproveTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ApproveTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ApproveTransactionArguments args) {
        // i. Owner != 0x
        ensure(!state.hasOwner(), Status.APPROVE_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.APPROVE_VALUE_LESS_THAN_ZERO);

        // iii. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.APPROVE_CALLER_TRANSFER_NOT_ALLOWED);

        // iv. CheckTransferAllowed(spender)
        ensure(state.checkTransferAllowed(args.spender), Status.APPROVE_SPENDER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void validatePost(State state, Address caller, ApproveTransactionArguments args) {
        // i. Allowances[caller][spender] = value
        assert state.getAllowance(caller, args.spender).equals(args.value);
    }

    @Override
    protected void updateState(State state, Address caller, ApproveTransactionArguments args) {
        state.setAllowance(caller, args.spender, args.value);
    }
}
