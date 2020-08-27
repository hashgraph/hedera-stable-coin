package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ClaimOwnershipTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ClaimOwnershipTransactionHandler extends TransactionHandler<ClaimOwnershipTransactionArguments> {
    @Override
    public ClaimOwnershipTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ClaimOwnershipTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ClaimOwnershipTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner != 0x
        ensureOwnerSet(state);

        // ii. caller = ProposedOwner
        ensureAuthorized(caller.equals(state.getProposedOwner()));

        // iii. CheckTransferAllowed(caller)
        ensureCallerTransferAllowed(state, caller);
    }

    @Override
    protected void updateState(State state, Address caller, ClaimOwnershipTransactionArguments args) {
        // i. Owner = caller
        state.setOwner(caller);

        // ii. ProposedOwner = 0x
        state.setProposedOwner(Address.ZERO);
    }
}
