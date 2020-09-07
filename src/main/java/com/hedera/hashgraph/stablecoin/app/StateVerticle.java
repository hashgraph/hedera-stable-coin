package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

import java.math.BigInteger;
import java.util.Map;

public class StateVerticle extends AbstractVerticle {
    private final State state;

    StateVerticle(State state) {
        this.state = state;
    }

    @Override
    public void start(Promise<Void> promise) {
        var server = vertx.createHttpServer();
        var router = Router.router(vertx);

        router.route().handler(CorsHandler.create("*")).failureHandler(StateVerticle::failureHandler);

        router.get("/").handler(this::getToken);
        router.get("/:address").handler(this::getAddress);
        router.get("/:address/allowance/:of").handler(this::getAllowance);
        router.get("/:address/balance").handler(this::getBalance);

        server
            .requestHandler(router)
            .listen(config().getInteger("http.port", 9000), result -> {
                if (result.succeeded()) {
                    promise.complete();
                } else {
                    promise.fail(result.cause());
                }
            });
    }

    private void getToken(RoutingContext routingContext) {
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

    private void getBalance(RoutingContext routingContext) {
        var address = Address.fromString(routingContext.request().getParam("address"));
        var balance = state.getBalanceOf(address);

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(new JsonObject(Map.of("balance", balance.toString())).encode());
    }

    private void getAllowance(RoutingContext routingContext) {
        var address = Address.fromString(routingContext.request().getParam("address"));
        var of = Address.fromString(routingContext.request().getParam("of"));

        BigInteger allowance = state.getAllowance(address, of);

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(new JsonObject(Map.of("allowance", allowance.toString())).encode());
    }

    private void getAddress(RoutingContext routingContext) {
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

    private static void failureHandler(RoutingContext routingContext) {
        var response = routingContext.response();

        // if we got into the failure handler the status code
        // has likely been populated
        if (routingContext.statusCode() > 0) {
            response.setStatusCode(routingContext.statusCode());
        }

        var cause = routingContext.failure();
        if (cause != null) {
            cause.printStackTrace();
            response.setStatusCode(500);
        }

        response.end();
    }
}
