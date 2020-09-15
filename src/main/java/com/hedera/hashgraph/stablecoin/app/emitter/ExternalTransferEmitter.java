package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ExternalTransferEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class ExternalTransferEmitter extends AbstractEmitter<ExternalTransferTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, ExternalTransferTransactionArguments args) {
        var event = Event.newBuilder()
            .setExternalTransfer(ExternalTransferEventData.newBuilder()
                .setAmount(ByteString.copyFrom(args.amount.toByteArray()))
                .setNetworkURI(args.networkURI)
                .setFrom(ByteString.copyFrom(transactionId.address.toBytes()))
                .setTo(args.to))
            .build();

        publish(event);
    }
}
