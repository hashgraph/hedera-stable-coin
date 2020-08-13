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
        ensure(state.hasOwner(), Status.TRANSFER_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.TRANSFER_VALUE_LESS_THAN_ZERO);

        // iii. Balances[caller] >= value
        ensure(state.getBalanceOf(caller).compareTo(args.value) >= 0, Status.TRANSFER_INSUFFICIENT_BALANCE);

        // iv. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.TRANSFER_CALLER_TRANSFER_NOT_ALLOWED);

        // v. CheckTransferAllowed(to)
        ensure(state.checkTransferAllowed(args.to), Status.TRANSFER_TO_ADDRESS_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, TransferTransactionArguments args) {
        // i. Balances[caller]’ = Balances[caller] - value
        var callerBalancePrime = state.getBalanceOf(caller).subtract(args.value);

        // ii. Balances[to]' = Balances[to] + value
        var toBalancePrime = state.getBalanceOf(args.to).add(args.value);

        state.setBalance(caller, callerBalancePrime);
        state.setBalance(args.to, toBalancePrime);
    }
}
