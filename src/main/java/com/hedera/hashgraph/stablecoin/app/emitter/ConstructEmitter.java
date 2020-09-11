package com.hedera.hashgraph.stablecoin.app.emitter;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ConstructTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.proto.ConstructEventData;
import com.hedera.hashgraph.stablecoin.sdk.Address;

public class ConstructEmitter extends AbstractEmitter<ConstructTransactionArguments> {
    @Override
    public void emit(State state, Address caller, ConstructTransactionArguments args) {
        var event = Event.newBuilder()
            .setConstruct(ConstructEventData.newBuilder()
                .setTokenName(args.tokenName)
                .setTokenSymbol(args.tokenSymbol)
                .setTokenDecimal(args.tokenDecimal)
                .setTotalSupply(ByteString.copyFrom(args.totalSupply.toByteArray()))
                .setSupplyManager(ByteString.copyFrom(args.supplyManager.toBytes()))
                .setComplianceManager(ByteString.copyFrom(args.complianceManager.toBytes()))
                .setEnforcementManager(ByteString.copyFrom(args.enforcementManager.toBytes()))
            ).build();

        publish(event);
    }
}
