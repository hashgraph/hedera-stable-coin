package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.WipeTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_WIPE;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class WipeTransactionDataRepository extends TransactionDataRepository<WipeTransactionArguments> {
    WipeTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.WIPE;
    }

    @Override
    public Collection<Address> getAddressList(WipeTransactionArguments arguments) {
        return Collections.singletonList(arguments.address);
    }

    @Override
    public JsonObject toTransactionData(WipeTransactionArguments arguments) {
        return new JsonObject(ofEntries(
            entry("address", arguments.address.toString()),
            entry("value", arguments.value.toString())
        ));
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_WIPE,
            TRANSACTION_WIPE.TIMESTAMP,
            TRANSACTION_WIPE.ADDRESS,
            TRANSACTION_WIPE.VALUE
        ).values((Long) null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, WipeTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.address.toBytes(),
            arguments.value
        );
    }
}
