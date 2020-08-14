package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import io.grpc.netty.shaded.io.netty.channel.AdaptiveRecvByteBufAllocator;

import java.math.BigInteger;

public abstract class TransactionHandler<ArgumentsT> {
    protected abstract ArgumentsT parseArguments(TransactionBody transactionBody);

    protected abstract void validatePre(State state, Address caller, ArgumentsT args);

    protected abstract void updateState(State state, Address caller, ArgumentsT args);

    protected void ensure(boolean condition, Status status) {
        // todo: log transaction as failed rather than exploding
        if (!condition) {
            throw new IllegalStateException("pre-condition failed with status " + status);
        }
    }

    protected void ensureOwnerSet(State state) {
        ensure(!state.getOwner().isZero(), Status.OWNER_NOT_SET);
    }

    protected void ensureAuthorized(boolean isAuthorized) {
        ensure(isAuthorized, Status.CALLER_NOT_AUTHORIZED);
    }

    protected void ensureAssetProtectionManager(State state, Address caller) {
        ensureAuthorized(caller.equals(state.getAssetProtectionManager()) || caller.equals(state.getOwner()));
    }

    protected void ensureSupplyManager(State state, Address caller) {
        ensureAuthorized(caller.equals(state.getSupplyManager()) || caller.equals(state.getOwner()));
    }

    protected void ensureZeroOrGreater(BigInteger value, Status status) {
        ensureEqualOrGreater(value, BigInteger.ZERO, status);
    }

    protected void ensureEqualOrGreater(BigInteger value, BigInteger equalTo, Status status) {
        ensure(value.compareTo(equalTo) >= 0, status);
    }

    protected void ensureTransferAllowed(State state, Address address, Status status) {
        ensure(state.checkTransferAllowed(address), status);
    }

    protected void ensureCallerTransferAllowed(State state, Address caller) {
        ensureTransferAllowed(state, caller, Status.CALLER_TRANSFER_NOT_ALLOWED);
    }

    public void handle(State state, Address caller, TransactionBody transactionBody) {
        var arguments = parseArguments(transactionBody);

        // check pre-conditions
        // fixme: if these fail, mark the transaction as failed with the given status from the exception
        validatePre(state, caller, arguments);

        // now update our state, this should not be able to fail
        updateState(state, caller, arguments);
    }
}
