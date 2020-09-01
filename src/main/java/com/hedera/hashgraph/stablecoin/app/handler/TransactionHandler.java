package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.math.BigInteger;

public abstract class TransactionHandler<ArgumentsT> {
    public abstract ArgumentsT parseArguments(TransactionBody transactionBody);

    protected abstract void validatePre(State state, Address caller, ArgumentsT args) throws StableCoinPreCheckException;

    protected abstract void updateState(State state, Address caller, ArgumentsT args);

    protected void ensure(boolean condition, Status status) throws StableCoinPreCheckException {
        if (!condition) {
            throw new StableCoinPreCheckException(status);
        }
    }

    protected void ensureOwnerSet(State state) throws StableCoinPreCheckException {
        ensure(!state.getOwner().isZero(), Status.OWNER_NOT_SET);
    }

    protected void ensureAuthorized(boolean isAuthorized) throws StableCoinPreCheckException {
        ensure(isAuthorized, Status.CALLER_NOT_AUTHORIZED);
    }

    protected void ensureAssetProtectionManager(State state, Address caller) throws StableCoinPreCheckException {
        ensureAuthorized(caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()));
    }

    protected void ensureSupplyManager(State state, Address caller) throws StableCoinPreCheckException {
        ensureAuthorized(caller.equals(state.getSupplyManager()));
    }

    protected void ensureSupplyManagerOrOwner(State state, Address caller) throws StableCoinPreCheckException {
        ensureAuthorized(caller.equals(state.getSupplyManager()) || caller.equals(state.getOwner()));
    }

    protected void ensureZeroOrGreater(BigInteger value, Status status) throws StableCoinPreCheckException {
        ensureEqualOrGreater(value, BigInteger.ZERO, status);
    }

    protected void ensureZeroOrGreater(int value, Status status) throws StableCoinPreCheckException {
        ensure(value >= 0, status);
    }

    protected void ensureEqualOrGreater(BigInteger value, BigInteger equalTo, Status status) throws StableCoinPreCheckException {
        ensure(value.compareTo(equalTo) >= 0, status);
    }

    protected void ensureTransferAllowed(State state, Address address, Status status) throws StableCoinPreCheckException {
        ensure(state.checkTransferAllowed(address), status);
    }

    protected void ensureCallerTransferAllowed(State state, Address caller) throws StableCoinPreCheckException {
        ensureTransferAllowed(state, caller, Status.CALLER_TRANSFER_NOT_ALLOWED);
    }

    public void handle(
        State state,
        Address caller,
        ArgumentsT arguments
    ) throws StableCoinPreCheckException {
        // check pre-conditions
        validatePre(state, caller, arguments);

        // acquire a lock to update our state
        // we do this to block a snapshot from happening in the middle of
        // a state update
        state.lock();

        try {
            // now update our state, this should not be able to fail
            updateState(state, caller, arguments);
        } finally {
            // release our state lock so that a snapshot may now happen
            state.unlock();
        }
    }
}
