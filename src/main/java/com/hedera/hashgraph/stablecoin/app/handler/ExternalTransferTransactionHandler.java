package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.util.Arrays;

public final class ExternalTransferTransactionHandler extends TransactionHandler<ExternalTransferTransactionArguments> {
    @Override
    public ExternalTransferTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ExternalTransferTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ExternalTransferTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = SupplyManager || caller = Owner
        ensureSupplyManagerOrOwner(state, caller);

        // iii. amount >= 0
        ensureZeroOrGreater(args.amount, Status.APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO);

        // iv. CheckTransferAllowed(from)
        ensureTransferAllowed(state, args.from, Status.EXTERNAL_TRANSFER_NOT_ALLOWED);

        // v. value <= MAX_INT
        ensureLessThanMaxInt(args.amount, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // vi. externalAllowanceOf(args.from, args.networkURI, args.to) >= value
        var allowance = state.getExternalAllowance(args.from, args.networkURI, args.to.toByteArray());
        ensureEqualOrGreater(allowance, args.amount, Status.EXTERNAL_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ExternalTransferTransactionArguments args) {
        // i. TotalSupply’ = TotalSupply - value
        state.decreaseTotalSupply(args.amount);

        // ii. Balances[from]’ = Balances[from] - value
        state.decreaseBalanceOf(args.from, args.amount);

        // iii. Allowances[(from, networkURI, to)] -= value
        var allowance = state.getExternalAllowance(args.from, args.networkURI, args.to.toByteArray());
        state.setExternalAllowance(args.from, args.networkURI, args.to.toByteArray(), allowance.subtract(args.amount));
    }
}
