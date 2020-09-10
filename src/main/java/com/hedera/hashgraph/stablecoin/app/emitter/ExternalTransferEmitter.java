package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ExternalTransferEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ExternalTransferEmitter extends AbstractEmitter<ExternalTransferTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ExternalTransferTransactionArguments args) {
        var event = Event.newBuilder()
            .setExternalTransfer(ExternalTransferEventData.newBuilder()
                .setAmount(ByteString.copyFrom(args.amount.toByteArray()))
                .setNetworkURI(args.networkURI)
                .setFrom(ByteString.copyFrom(caller.publicKey.toBytes()))
                .setTo(args.to))
            .build();

        publish(event);
    }
}
