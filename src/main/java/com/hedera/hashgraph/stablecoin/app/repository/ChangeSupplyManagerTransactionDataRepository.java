package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeSupplyManagerTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_CHANGE_SUPPLY_MANAGER;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class ChangeSupplyManagerTransactionDataRepository extends TransactionDataRepository<ChangeSupplyManagerTransactionArguments> {
    ChangeSupplyManagerTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.CHANGE_SUPPLY_MANAGER;
    }

    @Override
    public Collection<Address> getAddressList(ChangeSupplyManagerTransactionArguments arguments) {
        return Collections.singletonList(arguments.address);
    }

    @Override
    public JsonObject toTransactionData(ChangeSupplyManagerTransactionArguments arguments) {
        return new JsonObject(ofEntries(
            entry("address", arguments.address.toString())
        ));
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_CHANGE_SUPPLY_MANAGER,
            TRANSACTION_CHANGE_SUPPLY_MANAGER.TIMESTAMP,
            TRANSACTION_CHANGE_SUPPLY_MANAGER.ADDRESS
        ).values((Long) null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ChangeSupplyManagerTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.address.toBytes()
        );
    }
}
