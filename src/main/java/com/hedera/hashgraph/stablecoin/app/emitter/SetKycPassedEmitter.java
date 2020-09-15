package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.SetKycPassedTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.SetKycPassedEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class SetKycPassedEmitter extends AbstractEmitter<SetKycPassedTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, SetKycPassedTransactionArguments args) {
        var event = Event.newBuilder()
            .setSetKycPassed(SetKycPassedEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
