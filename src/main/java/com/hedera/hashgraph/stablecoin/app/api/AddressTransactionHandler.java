package com.hedera.hashgraph.stablecoin.app.api;

import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AddressTransactionHandler implements Handler<RoutingContext> {
    private final PgPool pgPool;

    private final String sql = CharStreams.toString(new InputStreamReader(Objects.requireNonNull(
        TransactionHandler.class.getClassLoader().getResourceAsStream("sql/latest-50-transactions-for-address.sql")), UTF_8));

    AddressTransactionHandler(PgPool pgPool) throws IOException {
        this.pgPool = pgPool;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var address = routingContext.request().getParam("address");

        pgPool.preparedQuery(sql).execute(Tuple.of(address), ar -> {
            if (ar.failed()) {
                routingContext.fail(ar.cause());
                return;
            }

            try {
                var rows = ar.result();
                var transactions = new ArrayList<TransactionResponseItem>(rows.rowCount());

                for (var row : rows) {
                    var item = new TransactionResponseItem();

                    item.caller = BaseEncoding.base16().lowerCase().encode(row.getBuffer("caller").getBytes());
                    item.consensusAt = Instant.ofEpochSecond(0, row.getLong("timestamp"));
                    item.data = row.get(JsonObject.class, 4);
                    item.transaction = row.getString("transaction");
                    item.status = Status.valueOf(row.getInteger("status"));

                    transactions.add(item);
                }

                var response = new TransactionResponse();
                response.transactions = transactions;

                routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encodeToBuffer(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static class TransactionResponse {
        public List<TransactionResponseItem> transactions = new ArrayList<>();
    }

    private static class TransactionResponseItem {
        public Instant consensusAt = Instant.EPOCH;

        public String caller = "";

        public Status status = Status.OK;

        public String transaction = "";

        public JsonObject data = null;
    }
}
