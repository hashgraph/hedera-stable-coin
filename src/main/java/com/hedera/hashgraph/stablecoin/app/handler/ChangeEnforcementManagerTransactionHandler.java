package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeEnforcementManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public final class ChangeEnforcementManagerTransactionHandler extends TransactionHandler<ChangeEnforcementManagerTransactionArguments> {
    @Override
    public ChangeEnforcementManagerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ChangeEnforcementManagerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ChangeEnforcementManagerTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = Owner
        ensureAuthorized(caller.equals(state.getOwner()));

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.CHANGE_ENFORCEMENT_MANAGER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensureTransferAllowed(state, args.address, Status.CHANGE_ENFORCEMENT_MANAGER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ChangeEnforcementManagerTransactionArguments args) {
        // i. EnforcementManager = addr
        state.setEnforcementManager(args.address);
    }
}
