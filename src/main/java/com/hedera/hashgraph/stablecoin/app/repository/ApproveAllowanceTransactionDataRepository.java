package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ApproveAllowanceTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_APPROVE_ALLOWANCE;

public final class ApproveAllowanceTransactionDataRepository extends TransactionDataRepository<ApproveAllowanceTransactionArguments> {
    ApproveAllowanceTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.APPROVE_ALLOWANCE;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_APPROVE_ALLOWANCE,
            TRANSACTION_APPROVE_ALLOWANCE.TIMESTAMP,
            TRANSACTION_APPROVE_ALLOWANCE.SPENDER,
            TRANSACTION_APPROVE_ALLOWANCE.VALUE
        ).values((Long) null, null, null));
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ApproveAllowanceTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.spender.toBytes(),
            arguments.value
        );
    }
}
