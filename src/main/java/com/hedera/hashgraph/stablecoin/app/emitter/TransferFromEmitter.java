package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.TransferEventData;
import com.hedera.hashgraph.stablecoin.proto.ApproveEventData;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

public class TransferFromEmitter extends AbstractEmitter<TransferFromTransactionArguments> {
    @Override
    public void emit(State state, TransactionId transactionId, TransferFromTransactionArguments args) {
        var transfer = Event.newBuilder()
            .setTransfer(TransferEventData.newBuilder()
                .setFrom(ByteString.copyFrom(args.from.toBytes()))
                .setTo(ByteString.copyFrom(args.to.toBytes()))
                .setValue(ByteString.copyFrom(args.value.toByteArray()))
            ).build();

        var approve = Event.newBuilder()
            .setApprove(ApproveEventData.newBuilder()
                .setFrom(ByteString.copyFrom(args.from.toBytes()))
                .setTo(ByteString.copyFrom(transactionId.address.toBytes()))
                .setValue(ByteString.copyFrom(state.getAllowance(args.from, transactionId.address).toByteArray()))
            ).build();

        publish(transfer);
        publish(approve);
    }
}
