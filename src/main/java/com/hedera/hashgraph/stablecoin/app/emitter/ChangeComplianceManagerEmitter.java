package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeComplianceManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ChangeComplianceManagerEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ChangeComplianceManagerEmitter extends AbstractEmitter<ChangeComplianceManagerTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ChangeComplianceManagerTransactionArguments args) {
        var event = Event.newBuilder()
            .setChangeComplianceManager(ChangeComplianceManagerEventData.newBuilder()
                .setAddress(ByteString.copyFrom(args.address.toBytes()))
            ).build();

        publish(event);
    }
}
