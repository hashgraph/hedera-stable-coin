package com.hedera.hashgraph.stablecoin.app.emitter;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.api.EventHandler;
import com.hedera.hashgraph.stablecoin.proto.Event;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;

public abstract class AbstractEmitter<ArgumentsT> {
    public abstract void emit(State state, Address caller, ArgumentsT args);

    protected void publish(Event event) {
        for (ServerWebSocket webSocket : EventHandler.webSockets) {
            webSocket.writeFinalBinaryFrame(Buffer.buffer(event.toByteArray()));
        }
    }
}
