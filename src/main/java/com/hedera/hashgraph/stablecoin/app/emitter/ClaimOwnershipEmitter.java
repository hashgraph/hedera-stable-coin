package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ClaimOwnershipTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ClaimOwnershipEventData;
import com.hedera.hashgraph.stablecoin.proto.ProposeOwnerEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ClaimOwnershipEmitter extends AbstractEmitter<ClaimOwnershipTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ClaimOwnershipTransactionArguments args) {
        var claim = Event.newBuilder()
            .setClaimOwnership(ClaimOwnershipEventData.newBuilder()
                .setCaller(ByteString.copyFrom(caller.toBytes()))
            ).build();

        var propose = Event.newBuilder()
            .setProposeOwner(ProposeOwnerEventData.newBuilder()
                .setAddress(ByteString.copyFrom(Address.ZERO.toBytes()))
            ).build();

        publish(claim);
        publish(propose);
    }
}
