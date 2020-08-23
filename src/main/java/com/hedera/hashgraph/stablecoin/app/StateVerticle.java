package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.math.BigInteger;

public class StateVerticle extends AbstractVerticle {
    private final State state;

    StateVerticle(State state) {
        this.state = state;
    }

    @Override
    public void start(Promise<Void> promise) {

        Router router = Router.router(vertx);

        router.route("/").handler(rc -> {
            HttpServerResponse response = rc.response();
            response
                .putHeader("content-type", "text/html")
                .end("</pre><h1>State Api</h1> <pre>");
        });

        router.get("/api/tokenname").handler(this::getTokenName);
        router.get("/api/tokensymbol").handler(this::getTokenSymbol);
        router.get("/api/tokendecimal").handler(this::getTokenDecimal);
        router.get("/api/totalsupply").handler(this::getTotalSupply);
        router.get("/api/balance/:address").handler(this::getBalance);
        router.get("/api/allowance/:caller/:address").handler(this::getAllowance);
        router.get("/api/owner").handler(this::getOwner);
        router.get("/api/supplymanager").handler(this::getSupplyManager);
        router.get("/api/assetprotectionmanager").handler(this::getAssetProtectionManager);
        router.get("/api/proposedowner").handler(this::getProposedOwner);
        router.get("/api/isfrozen/:address").handler(this::isFrozen);
        router.get("/api/iskycpassed/:address").handler(this::isKycPassed);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(config().getInteger("http.port", 8080), result -> {
                if (result.succeeded()) {
                    promise.complete();
                } else {
                    promise.fail(result.cause());
                }
            });
    }

    private void getTokenName(RoutingContext routingContext) {
        String name = state.getTokenName();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(name));
    }

    private void getTokenSymbol(RoutingContext routingContext) {
        String symbol = state.getTokenSymbol();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(symbol));
    }

    private void getTokenDecimal(RoutingContext routingContext) {
        BigInteger decimal = state.getTokenDecimal();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(decimal.toString()));
    }

    private void getTotalSupply(RoutingContext routingContext) {
        BigInteger supply = state.getTotalSupply();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(supply.toString()));
    }

    private void getOwner(RoutingContext routingContext) {
        Address owner = state.getOwner();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(owner.toString()));
    }

    private void getSupplyManager(RoutingContext routingContext) {
        Address manager = state.getSupplyManager();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(manager.toString()));
    }

    private void getAssetProtectionManager(RoutingContext routingContext) {
        Address manager = state.getAssetProtectionManager();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(manager.toString()));
    }

    private void getProposedOwner(RoutingContext routingContext) {
        Address owner = state.getProposedOwner();

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(owner.toString()));
    }

    private void getBalance(RoutingContext routingContext) {
        String address = routingContext.request().getParam("address");
        BigInteger balance = state.getBalanceOf(new Address(PublicKey.fromString(address)));

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(balance.toString()));
    }

    private void getAllowance(RoutingContext routingContext) {
        String caller = routingContext.request().getParam("caller");
        String address = routingContext.request().getParam("address");
        BigInteger allowance = state.getAllowance(new Address(PublicKey.fromString(caller)), new Address(PublicKey.fromString(address)));

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(allowance.toString()));
    }

    private void isFrozen(RoutingContext routingContext) {
        String address = routingContext.request().getParam("address");
        boolean isFrozen = state.isFrozen(new Address(PublicKey.fromString(address)));

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(Boolean.toString(isFrozen)));
    }

    private void isKycPassed(RoutingContext routingContext) {
        String address = routingContext.request().getParam("address");
        boolean isKycPassed = state.isKycPassed(new Address(PublicKey.fromString(address)));

        routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end(Json.encodePrettily(Boolean.toString(isKycPassed)));
    }

}
