package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.WipeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.WipeEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class WipeEmitter extends AbstractEmitter<WipeTransactionArguments> {
    @Override
    public void emit(State state, Address caller, WipeTransactionArguments args) {
        var event = Event.newBuilder()
            .setWipe(WipeEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
