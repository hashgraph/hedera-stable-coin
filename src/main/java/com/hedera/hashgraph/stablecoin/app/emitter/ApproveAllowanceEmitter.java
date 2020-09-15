package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ApproveEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class ApproveAllowanceEmitter extends AbstractEmitter<ApproveAllowanceTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, ApproveAllowanceTransactionArguments args) {
        var event = Event.newBuilder()
            .setApprove(ApproveEventData.newBuilder()
                .setFrom(ByteString.copyFrom(transactionId.address.toBytes()))
                .setTo(ByteString.copyFrom(args.spender.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        publish(event);
    }
}
