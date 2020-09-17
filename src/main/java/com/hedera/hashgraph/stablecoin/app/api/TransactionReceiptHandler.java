package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.time.Instant;

public class TransactionReceiptHandler implements Handler<RoutingContext> {
    private final State state;

    TransactionReceiptHandler(State state) {
        this.state = state;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var operatorAccountNum = Long.parseLong(routingContext.request().getParam("operatorAccountNum"));
        var validStartNanos = Long.parseLong(routingContext.request().getParam("validStartNanos"));
        var validStart = Instant.ofEpochSecond(0, validStartNanos);

        var transactionId = TransactionId.withValidStart(
            new AccountId(operatorAccountNum), validStart);

        var receipt = state.getTransactionReceipt(transactionId);

        if (receipt == null) {
            routingContext.response()
                .setStatusCode(404)
                .end();

            return;
        }

        var response = new TransactionReceiptResponse();
        response.id = operatorAccountNum + "/" + validStartNanos;
        response.caller = receipt.caller.toString();
        response.consensusAt = receipt.consensusAt;
        response.status = receipt.status;

        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(Json.encodeToBuffer(response));
    }

    private static class TransactionReceiptResponse {
        public String id = "";

        public String caller = "";

        public Instant consensusAt = Instant.EPOCH;

        public Status status = Status.OK;
    }
}
