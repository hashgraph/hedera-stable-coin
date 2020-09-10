package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ApproveExternalTransferEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ApproveExternalTransferEmitter extends AbstractEmitter<ApproveExternalTransferTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ApproveExternalTransferTransactionArguments args) {
        var event = Event.newBuilder()
            .setApproveExternalTransfer(ApproveExternalTransferEventData.newBuilder()
                .setAmount(ByteString.copyFrom(args.amount.toByteArray()))
                .setNetworkURI(args.networkURI)
                .setFrom(ByteString.copyFrom(caller.publicKey.toBytes()))
                .setTo(args.to))
            .build();

        publish(event);
    }
}
