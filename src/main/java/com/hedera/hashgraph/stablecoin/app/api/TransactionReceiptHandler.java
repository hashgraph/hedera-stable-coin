package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;
import com.hedera.hashgraph.stablecoin.sdk.Address;

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
        var address = Address.fromString(routingContext.request().getParam("address"));
        var id = Long.parseLong(routingContext.request().getParam("id"));

        var transactionId = new TransactionId(address, Instant.ofEpochSecond(0, id));
        var receipt = state.getTransactionReceipt(transactionId);

        if (receipt == null) {
            routingContext.response()
                .setStatusCode(404)
                .end();

            return;
        }

        var response = new TransactionReceiptResponse();
        response.id = Long.toString(id);
        response.caller = address.toString();
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
