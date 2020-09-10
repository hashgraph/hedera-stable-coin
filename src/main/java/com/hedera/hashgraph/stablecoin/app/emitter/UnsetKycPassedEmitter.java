package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.UnsetKycPassedTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.UnsetKycPassedEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class UnsetKycPassedEmitter extends AbstractEmitter<UnsetKycPassedTransactionArguments> {
    @Override
    public void emit(State state, Address caller, UnsetKycPassedTransactionArguments args) {
        var event = Event.newBuilder()
            .setUnsetKycPassed(UnsetKycPassedEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
