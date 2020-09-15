package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeEnforcementManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ChangeEnforcementManagerEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class ChangeEnforcementManagerEmitter extends AbstractEmitter<ChangeEnforcementManagerTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, ChangeEnforcementManagerTransactionArguments args) {
        var event = Event.newBuilder()
            .setChangeEnforcementManager(ChangeEnforcementManagerEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
