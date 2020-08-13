package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.ProposeOwnerTransactionArguments;

public final class ProposeOwnerTransactionHandler extends TransactionHandler<ProposeOwnerTransactionArguments> {
    @Override
    protected ProposeOwnerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ProposeOwnerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ProposeOwnerTransactionArguments args) {
        // i. Owner != 0x
        ensure(state.hasOwner(), Status.PROPOSE_OWNER_OWNER_NOT_SET);

        // ii. caller = Owner
        ensure(caller.equals(state.getOwner()), Status.PROPOSE_OWNER_CALLER_NOT_OWNER);

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.PROPOSE_OWNER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensure(state.checkTransferAllowed(args.address), Status.PROPOSE_OWNER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ProposeOwnerTransactionArguments args) {
        // i. ProposedOwner = addr
        state.setProposeOwner(args.address);
    }
}
