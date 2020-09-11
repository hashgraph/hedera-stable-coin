package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ProposeOwnerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ProposeOwnerEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ProposeOwnerEmitter extends AbstractEmitter<ProposeOwnerTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ProposeOwnerTransactionArguments args) {
        var event = Event.newBuilder()
            .setProposeOwner(ProposeOwnerEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
