package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.MintTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.MintEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class MintEmitter extends AbstractEmitter<MintTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, MintTransactionArguments args) {
        var event = Event.newBuilder()
            .setMint(MintEventData.newBuilder()
                .setSupplyManager(ByteString.copyFrom(transactionId.address.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}

