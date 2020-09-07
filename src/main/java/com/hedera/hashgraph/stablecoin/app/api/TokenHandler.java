package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.State;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

public class TokenHandler implements Handler<RoutingContext> {
    private final State state;

    TokenHandler(State state) {
        this.state = state;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var name = state.getTokenName();
        var symbol = state.getTokenSymbol();
        var decimal = state.getTokenDecimal();
        var totalSupply = state.getTotalSupply();
        var owner = state.getOwner();
        var proposedOwner = state.getProposedOwner();
        var supplyManager = state.getSupplyManager();
        var complianceManager = state.getComplianceManager();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(new JsonObject(Map.ofEntries(
                Map.entry("name", name),
                Map.entry("symbol", symbol),
                Map.entry("decimals", decimal),
                Map.entry("totalSupply", totalSupply.toString()),
                Map.entry("owner", owner.toString()),
                Map.entry("proposedOwner", proposedOwner.toString()),
                Map.entry("supplyManager", supplyManager.toString()),
                Map.entry("complianceManager", complianceManager.toString())
            )).encode());
    }
}
