package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.TransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_TRANSFER_FROM;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public final class TransferFromTransactionDataRepository extends TransactionDataRepository<TransferFromTransactionArguments> {
    TransferFromTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.TRANSFER_FROM;
    }

    @Override
    public Collection<Address> getAddressList(TransferFromTransactionArguments arguments) {
        return List.of(arguments.from, arguments.to);
    }

    @Override
    public JsonObject toTransactionData(TransferFromTransactionArguments arguments) {
        return new JsonObject(ofEntries(
            entry("from", arguments.from.toString()),
            entry("to", arguments.to.toString()),
            entry("value", arguments.value.toString())
        ));
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_TRANSFER_FROM,
            TRANSACTION_TRANSFER_FROM.TIMESTAMP,
            TRANSACTION_TRANSFER_FROM.SENDER,
            TRANSACTION_TRANSFER_FROM.RECEIVER,
            TRANSACTION_TRANSFER_FROM.VALUE
        ).values((Long) null, null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, TransferFromTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.from.toBytes(),
            arguments.to.toBytes(),
            arguments.value
        );
    }
}
