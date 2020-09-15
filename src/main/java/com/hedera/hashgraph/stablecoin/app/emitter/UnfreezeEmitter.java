package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.UnfreezeTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.UnfreezeEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class UnfreezeEmitter extends AbstractEmitter<UnfreezeTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, UnfreezeTransactionArguments args) {
        var event = Event.newBuilder()
            .setUnfreeze(UnfreezeEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
