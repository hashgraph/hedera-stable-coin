package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class TransferTransactionHandler extends TransactionHandler<TransferTransactionArguments> {
    @Override
    protected TransferTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new TransferTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, TransferTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. value >= 0
        ensureZeroOrGreater(args.value, Status.TRANSFER_VALUE_LESS_THAN_ZERO);

        // iii. Balances[caller] >= value
        ensureEqualOrGreater(state.getBalanceOf(caller), args.value, Status.TRANSFER_INSUFFICIENT_BALANCE);

        // iv. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);

        // v. CheckTransferAllowed(to)
        ensureTransferAllowed(state, args.to, Status.TRANSFER_TO_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, TransferTransactionArguments args) {
        // i. Balances[caller]’ = Balances[caller] - value
        state.decreaseBalanceOf(caller, args.value);

        // ii. Balances[to]' = Balances[to] + value
        state.increaseBalanceOf(args.to, args.value);
    }
}
