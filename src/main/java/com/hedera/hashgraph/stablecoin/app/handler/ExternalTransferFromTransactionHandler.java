package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public final class ExternalTransferFromTransactionHandler extends TransactionHandler<ExternalTransferFromTransactionArguments> {
    @Override
    public ExternalTransferFromTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ExternalTransferFromTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ExternalTransferFromTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = SupplyManager || caller = Owner
        ensureSupplyManagerOrOwner(state, caller);

        // iii. amount >= 0
        ensureZeroOrGreater(args.amount, Status.APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iv. CheckTransferAllowed(to)
        ensureTransferAllowed(state, args.to, Status.EXTERNAL_TRANSFER_NOT_ALLOWED);

        // v. Balances[to] + value <= MAX_INT
        ensureLessThanMaxInt(state.getBalanceOf(args.to).add(args.amount), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // v. TotalSupply + value <= MAX_INT
        ensureLessThanMaxInt(state.getTotalSupply().add(args.amount), Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
    }

    @Override
    protected void updateState(State state, Address caller, ExternalTransferFromTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply + value
        state.increaseTotalSupply(args.amount);

        // ii. Balances[to]’ = Balances[to] + value
        state.increaseBalanceOf(args.to, args.amount);
    }
}
