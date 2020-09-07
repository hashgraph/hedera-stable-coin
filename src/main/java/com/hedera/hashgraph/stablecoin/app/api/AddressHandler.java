package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class AddressHandler implements Handler<RoutingContext> {
    private final State state;

    AddressHandler(State state) {
        this.state = state;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var address = Address.fromString(routingContext.request().getParam("address"));
        var balance = state.getBalanceOf(address);
        var isFrozen = state.isFrozen(address);
        var isKycPassed = state.isKycPassed(address);

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(new JsonObject(Map.ofEntries(
                Map.entry("balance", balance.toString()),
                Map.entry("isFrozen", isFrozen),
                Map.entry("isKycPassed", isKycPassed)
            )).encode());
    }
}
