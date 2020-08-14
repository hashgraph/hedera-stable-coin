package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.ClaimOwnershipTransactionArguments;

public final class ClaimOwnershipTransactionHandler extends TransactionHandler<ClaimOwnershipTransactionArguments> {
    @Override
    protected ClaimOwnershipTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ClaimOwnershipTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ClaimOwnershipTransactionArguments args) {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = ProposedOwner
        ensureAuthorized(caller.equals(state.getProposedOwner()));

        // iii. CheckTransferAllowed(addr)
        ensureTransferAllowed(state, args.address, Status.CLAIM_OWNERSHIP_TRANSFER_NOT_ALLOWED);
    }

    @Override
    protected void updateState(State state, Address caller, ClaimOwnershipTransactionArguments args) {
        // i. Owner = caller
        state.setOwner(caller);

        // ii. ProposedOwner = 0x
        state.setProposedOwner(Address.ZERO);
    }
}
