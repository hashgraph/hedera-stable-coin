package com.hedera.hashgraph.stablecoin.app.api;

import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
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

    private final TransactionRepository transactionRepository;

    private final String sql = CharStreams.toString(new InputStreamReader(Objects.requireNonNull(
        TransactionHandler.class.getClassLoader().getResourceAsStream("sql/latest-50-transactions-for-address.sql")), UTF_8));

    @SuppressWarnings("CheckedExceptionNotThrown")
    AddressTransactionHandler(PgPool pgPool, TransactionRepository transactionRepository) throws IOException {
        this.pgPool = pgPool;
        this.transactionRepository = transactionRepository;
    }

    private static void finish(RoutingContext routingContext, List<TransactionResponseItem> transactions) {
        var response = new TransactionResponse();
        response.transactions = transactions;

        routingContext.response()
            .putHeader("content-type", "application/json")
            .end(Json.encodeToBuffer(response));
    }

    @Override
    public void handle(RoutingContext routingContext) {
        var address = routingContext.request().getParam("address");

        var pending = transactionRepository.getPendingTransactionsFor(
            Address.fromString(address), 50);

        if (pending.size() >= 50) {
            finish(routingContext, pending);
            return;
        }

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

                transactions.addAll(0, pending);

                finish(routingContext, transactions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static class TransactionResponse {
        public List<TransactionResponseItem> transactions = new ArrayList<>();
    }
}
