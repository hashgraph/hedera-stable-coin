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
        ensure(state.hasOwner(), Status.TRANSFER_FROM_OWNER_NOT_SET);

        // ii. value >= 0
        ensure(args.value.compareTo(BigInteger.ZERO) >= 0, Status.TRANSFER_FROM_VALUE_LESS_THAN_ZERO);

        // iii. Balances[caller] >= value
        ensure(state.getBalanceOf(caller).compareTo(args.value) >= 0, Status.TRANSFER_FROM_INSUFFICIENT_BALANCE);

        ensure(state.getAllowance(args.from, caller).compareTo(args.value) >= 0, Status.TRANSFER_FROM_INSUFFICIENT_ALLOWANCE);

        // iv. CheckTransferAllowed(caller)
        ensure(state.checkTransferAllowed(caller), Status.TRANSFER_FROM_CALLER_TRANSFER_NOT_ALLOWED);

        // v. CheckTransferAllowed(from)
        ensure(state.checkTransferAllowed(args.from), Status.TRANSFER_FROM_FROM_ADDRESS_TRANSFER_NOT_ALLOWED);

        // vi. CheckTransferAllowed(to)
        ensure(state.checkTransferAllowed(args.to), Status.TRANSFER_FROM_TO_ADDRESS_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, TransferFromTransactionArguments args) {
        // i. Balances[from]’ = Balances[from] - value
        var fromBalancePrime = state.getBalanceOf(args.from).subtract(args.value);

        // ii. Allowances[from][caller]’ = Allowances[from][caller] - value
        var allowanceFromCallerPrime = state.getAllowance(args.from, caller).subtract(args.value);

        // iii. Balances[to]’ = Balances[to] + value
        var toBalancePrime = state.getBalanceOf(args.to).add(args.value);

        state.setBalance(args.from, fromBalancePrime);
        state.setAllowance(args.from, caller, allowanceFromCallerPrime);
        state.setBalance(args.to, toBalancePrime);
    }
}
