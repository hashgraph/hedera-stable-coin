package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.IncreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.IncreaseAllowanceEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class IncreaseAllowanceEmitter extends AbstractEmitter<IncreaseAllowanceTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, IncreaseAllowanceTransactionArguments args) {
        var event = Event.newBuilder()
            .setIncreaseAllowance(IncreaseAllowanceEventData.newBuilder()
                .setCaller(ByteString.copyFrom(transactionId.address.toBytes()))
                .setSpender(ByteString.copyFrom(args.spender.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
