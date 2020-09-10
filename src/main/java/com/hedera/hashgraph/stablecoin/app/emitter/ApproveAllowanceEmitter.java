package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ApproveEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ApproveAllowanceEmitter extends AbstractEmitter<ApproveAllowanceTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ApproveAllowanceTransactionArguments args) {
        var event = Event.newBuilder()
            .setApprove(ApproveEventData.newBuilder()
                .setFrom(ByteString.copyFrom(caller.toBytes()))
                .setTo(ByteString.copyFrom(args.spender.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
