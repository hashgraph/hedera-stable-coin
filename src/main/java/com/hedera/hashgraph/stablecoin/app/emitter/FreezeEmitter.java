package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.FreezeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.FreezeEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class FreezeEmitter extends AbstractEmitter<FreezeTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, FreezeTransactionArguments args) {
        var event = Event.newBuilder()
            .setFreeze(FreezeEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
