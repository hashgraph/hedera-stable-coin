package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ApproveExternalTransferEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class ApproveExternalTransferEmitter extends AbstractEmitter<ApproveExternalTransferTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, ApproveExternalTransferTransactionArguments args) {
        var event = Event.newBuilder()
            .setApproveExternalTransfer(ApproveExternalTransferEventData.newBuilder()
                .setAmount(ByteString.copyFrom(args.amount.toByteArray()))
                .setNetworkURI(args.networkURI)
                .setFrom(ByteString.copyFrom(transactionId.address.toBytes()))
                .setTo(args.to))
            .build();

        publish(event);
    }
}
