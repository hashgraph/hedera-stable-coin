package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.DecreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.DecreaseAllowanceEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class DecreaseAllowanceEmitter extends AbstractEmitter<DecreaseAllowanceTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, DecreaseAllowanceTransactionArguments args) {
        var event = Event.newBuilder()
            .setDecreaseAllowance(DecreaseAllowanceEventData.newBuilder()
                .setCaller(ByteString.copyFrom(transactionId.address.toBytes()))
                .setSpender(ByteString.copyFrom(args.spender.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
