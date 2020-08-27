package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ProposeOwnerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ProposeOwnerTransactionHandler extends TransactionHandler<ProposeOwnerTransactionArguments> {
    @Override
    public ProposeOwnerTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ProposeOwnerTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ProposeOwnerTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = Owner
        ensureAuthorized(caller.equals(state.getOwner()));

        // iii. addr != 0x
        ensure(!args.address.isZero(), Status.PROPOSE_OWNER_ADDRESS_NOT_SET);

        // iv. CheckTransferAllowed(addr)
        ensureTransferAllowed(state, args.address, Status.PROPOSE_OWNER_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ProposeOwnerTransactionArguments args) {
        // i. ProposedOwner = addr
        state.setProposedOwner(args.address);
    }
}
