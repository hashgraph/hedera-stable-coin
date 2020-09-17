package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.TransferEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class TransferEmitter extends AbstractEmitter<TransferTransactionArguments> {
    @Override
    public void emit(State state, Address caller, TransferTransactionArguments args) {
        var event = Event.newBuilder()
            .setTransfer(TransferEventData.newBuilder()
                .setFrom(ByteString.copyFrom(caller.toBytes()))
                .setTo(ByteString.copyFrom(args.to.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
