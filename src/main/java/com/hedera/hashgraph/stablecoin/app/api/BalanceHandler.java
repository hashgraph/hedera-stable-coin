package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class BalanceHandler implements Handler<RoutingContext> {
    private final State state;

    BalanceHandler(State state) {
        this.state = state;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var address = Address.fromString(routingContext.request().getParam("address"));
        var balance = state.getBalanceOf(address);

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(new JsonObject(Map.of("balance", balance.toString())).encode());
    }
}
