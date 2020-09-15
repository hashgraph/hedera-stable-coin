package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeSupplyManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ChangeSupplyManagerEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class ChangeSupplyManagerEmitter extends AbstractEmitter<ChangeSupplyManagerTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, ChangeSupplyManagerTransactionArguments args) {
        var event = Event.newBuilder()
            .setChangeSupplyManager(ChangeSupplyManagerEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
