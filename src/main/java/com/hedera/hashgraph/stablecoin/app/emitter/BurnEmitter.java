package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.BurnTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.BurnEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class BurnEmitter extends AbstractEmitter<BurnTransactionArguments> {
    @Override
    public void emit(State state, Address caller, BurnTransactionArguments args) {
        var event = Event.newBuilder()
            .setBurn(BurnEventData.newBuilder()
                .setSupplyManager(ByteString.copyFrom(caller.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
