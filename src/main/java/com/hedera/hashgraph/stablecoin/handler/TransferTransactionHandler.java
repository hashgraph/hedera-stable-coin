package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.TransferTransactionArguments;

import java.math.BigInteger;

public final class TransferTransactionHandler extends TransactionHandler<TransferTransactionArguments> {
    @Override
    protected TransferTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new TransferTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, TransferTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.OWNER_ZERO);

        // ii. value >= 0
        System.out.println("attempt to transfer " + args.value.toString());
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.TRANSFER_VALUE_LESS_THAN_ZERO);

        // iii. Balances[caller] >= value
        ensure(state.getBalanceOf(caller).compareTo(args.value) >= 0, Status.TRANSFER_INSUFFICIENT_BALANCE);

        // iv. CheckTransferAllowed(caller)
        ensureTransferAllowed(state, caller);

        // v. CheckTransferAllowed(to)
        ensureTransferAllowed(state, args.to);
    }

    @Override
    protected void validatePost(State state, Address caller, TransferTransactionArguments args) {
        // fixme: not sure how to solve this? maybe save the previous balance as an instance variable?

        // todo: i. Balances[caller]' = Balances[caller] - value

        // todo: ii. Balances[to]' = Balances[to] + value
    }

    @Override
    protected void updateState(State state, Address caller, TransferTransactionArguments args) {
        state.increaseBalanceOf(args.to, args.value);
        state.decreaseBalanceOf(caller, args.value);
    }
}
