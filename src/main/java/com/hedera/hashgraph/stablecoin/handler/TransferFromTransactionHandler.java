package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.TransferFromTransactionArguments;

import java.math.BigInteger;

public final class TransferFromTransactionHandler extends TransactionHandler<TransferFromTransactionArguments> {
    @Override
    protected TransferFromTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new TransferFromTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, TransferFromTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.TRANSFER_FROM_VALUE_LESS_THAN_ZERO);

        // iii. Balances[caller] >= value
        ensureEqualOrGreater(state.getBalanceOf(caller), args.value, Status.TRANSFER_FROM_INSUFFICIENT_BALANCE);

        // iv. Allowances[from][caller] >= value
        ensureEqualOrGreater(state.getAllowance(args.from, caller), args.value, Status.TRANSFER_FROM_INSUFFICIENT_ALLOWANCE);

        // v. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // vi. CheckTransferAllowed(from)
        ensureTransferAllowed(state, args.from, Status.TRANSFER_FROM_FROM_TRANSFER_NOT_ALLOWED);

        // vii. CheckTransferAllowed(to)
        ensureTransferAllowed(state, args.to, Status.TRANSFER_FROM_TO_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, TransferFromTransactionArguments args) {
        // i. Balances[from]’ = Balances[from] - value
        state.decreaseBalanceOf(args.from, args.value);

        // ii. Allowances[from][caller]’ = Allowances[from][caller] - value
        state.decreaseAllowanceOf(args.from, caller, args.value);

        // iii. Balances[to]’ = Balances[to] + value
        state.increaseBalanceOf(args.to, args.value);
    }
}
