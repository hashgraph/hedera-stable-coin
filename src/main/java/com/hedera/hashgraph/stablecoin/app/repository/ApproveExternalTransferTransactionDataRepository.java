package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveExternalTransferTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_APPROVE_EXTERNAL_TRANSFER;

public final class ApproveExternalTransferTransactionDataRepository extends TransactionDataRepository<ApproveExternalTransferTransactionArguments> {
    ApproveExternalTransferTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.APPROVE_EXTERNAL_TRANSFER;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_APPROVE_EXTERNAL_TRANSFER,
            TRANSACTION_APPROVE_EXTERNAL_TRANSFER.TIMESTAMP,
            TRANSACTION_APPROVE_EXTERNAL_TRANSFER.NETWORK_URI,
            TRANSACTION_APPROVE_EXTERNAL_TRANSFER.ADDRESS_TO,
            TRANSACTION_APPROVE_EXTERNAL_TRANSFER.AMOUNT
        ).values((Long) null, null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ApproveExternalTransferTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.networkURI,
            arguments.to.toByteArray(),
            arguments.amount
        );
    }
}
