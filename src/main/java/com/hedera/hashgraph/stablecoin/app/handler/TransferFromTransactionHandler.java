package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class TransferFromTransactionHandler extends TransactionHandler<TransferFromTransactionArguments> {
    @Override
    public TransferFromTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new TransferFromTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, TransferFromTransactionArguments args) throws StableCoinPreCheckException {
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

        // viii. value <= MAX_INT
        ensureLessThanMaxInt(args.value, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // ix. Balances[to] + value <= MAX_INT
        ensureLessThanMaxInt(state.getBalanceOf(args.to).add(args.value), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
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
