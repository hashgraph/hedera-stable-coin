package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeComplianceManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeComplianceManagerTransactionHandler extends TransactionHandler<ChangeComplianceManagerTransactionArguments> {
    @Override
    public ChangeComplianceManagerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ChangeComplianceManagerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ChangeComplianceManagerTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = Owner
        ensureAuthorized(caller.equals(state.getOwner()));

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.CHANGE_COMPLIANCE_MANAGER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensureTransferAllowed(state, args.address, Status.CHANGE_COMPLIANCE_MANAGER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ChangeComplianceManagerTransactionArguments args) {
        // i. complianceManager = addr
        state.setComplianceManager(args.address);
    }
}
