package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.IncreaseAllowanceTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_INCREASE_ALLOWANCE;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class IncreaseAllowanceTransactionDataRepository extends TransactionDataRepository<IncreaseAllowanceTransactionArguments> {
    IncreaseAllowanceTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.INCREASE_ALLOWANCE;
    }

    @Override
    public Collection<Address> getAddressList(IncreaseAllowanceTransactionArguments arguments) {
        return Collections.singletonList(arguments.spender);
    }

    @Override
    public JsonObject toTransactionData(IncreaseAllowanceTransactionArguments arguments) {
        return new JsonObject(ofEntries(
            entry("spender", arguments.spender.toString()),
            entry("value", arguments.value.toString())
        ));
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_INCREASE_ALLOWANCE,
            TRANSACTION_INCREASE_ALLOWANCE.TIMESTAMP,
            TRANSACTION_INCREASE_ALLOWANCE.SPENDER,
            TRANSACTION_INCREASE_ALLOWANCE.VALUE
        ).values((Long) null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, IncreaseAllowanceTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.spender.toBytes(),
            arguments.value
        );
    }
}
