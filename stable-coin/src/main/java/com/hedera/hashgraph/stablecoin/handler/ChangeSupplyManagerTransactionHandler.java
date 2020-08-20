package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.handler.arguments.ChangeSupplyManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ChangeSupplyManagerTransactionHandler extends TransactionHandler<ChangeSupplyManagerTransactionArguments> {
    @Override
    protected ChangeSupplyManagerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ChangeSupplyManagerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ChangeSupplyManagerTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = Owner
        ensureAuthorized(caller.equals(state.getOwner()));

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.CHANGE_SUPPLY_MANAGER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensure(state.checkTransferAllowed(args.address), Status.CHANGE_SUPPLY_MANAGER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ChangeSupplyManagerTransactionArguments args) {
        // i. SupplyManager = addr
        state.setSupplyManager(args.address);
    }
}
