package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_EXTERNAL_TRANSFER;

public final class ExternalTransferTransactionDataRepository extends TransactionDataRepository<ExternalTransferTransactionArguments> {
    ExternalTransferTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.EXTERNAL_TRANSFER;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_EXTERNAL_TRANSFER,
            TRANSACTION_EXTERNAL_TRANSFER.TIMESTAMP,
            TRANSACTION_EXTERNAL_TRANSFER.ADDRESS_FROM,
            TRANSACTION_EXTERNAL_TRANSFER.NETWORK_URI,
            TRANSACTION_EXTERNAL_TRANSFER.ADDRESS_TO,
            TRANSACTION_EXTERNAL_TRANSFER.AMOUNT
        ).values((Long) null, null, null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ExternalTransferTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.from.toBytes(),
            arguments.networkURI,
            arguments.to.toStringUtf8().getBytes(),
            arguments.amount
        );
    }
}
