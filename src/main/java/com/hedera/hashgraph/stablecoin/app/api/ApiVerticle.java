package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.pgclient.PgPool;

import java.io.IOException;

public class ApiVerticle extends AbstractVerticle {
    private final State state;

    private final PgPool pgPool;

    private final TransactionRepository transactionRepository;

    public ApiVerticle(State state, PgPool pgPool, TransactionRepository transactionRepository) {
        this.state = state;
        this.pgPool = pgPool;
        this.transactionRepository = transactionRepository;
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

    @Override
    public void start(Promise<Void> promise) throws IOException {
        var server = vertx.createHttpServer();
        var router = Router.router(vertx);

        router.route().handler(CorsHandler.create("*")).failureHandler(ApiVerticle::failureHandler);

        router.get("/").handler(new TokenHandler(state));
        router.get("/event").handler(new EventHandler());
        router.get("/transaction").handler(new TransactionHandler(pgPool, transactionRepository));
        router.get("/:address").handler(new AddressHandler(state));
        router.get("/:address/transaction").handler(new AddressTransactionHandler(pgPool, transactionRepository));
        router.get("/transaction/:operatorAccountNum/:validStartNanos/receipt").handler(new TransactionReceiptHandler(state));
        router.get("/:address/allowance/:of").handler(new AllowanceHandler(state));
        router.get("/:address/balance").handler(new BalanceHandler(state));

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
}
