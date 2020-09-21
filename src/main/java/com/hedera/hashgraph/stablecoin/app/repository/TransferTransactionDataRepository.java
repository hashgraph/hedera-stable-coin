package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_TRANSFER;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class TransferTransactionDataRepository extends TransactionDataRepository<TransferTransactionArguments> {
    TransferTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.TRANSFER;
    }

    @Override
    public Collection<Address> getAddressList(TransferTransactionArguments arguments) {
        return Collections.singletonList(arguments.to);
    }

    @Override
    public JsonObject toTransactionData(TransferTransactionArguments arguments) {
        return new JsonObject(ofEntries(
            entry("to", arguments.to.toString()),
            entry("value", arguments.value.toString())
        ));
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_TRANSFER,
            TRANSACTION_TRANSFER.TIMESTAMP,
            TRANSACTION_TRANSFER.RECEIVER,
            TRANSACTION_TRANSFER.VALUE
        ).values((Long) null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, TransferTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.to.toBytes(),
            arguments.value
        );
    }
}
