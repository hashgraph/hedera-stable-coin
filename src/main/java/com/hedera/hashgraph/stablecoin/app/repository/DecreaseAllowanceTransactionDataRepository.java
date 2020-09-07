package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.DecreaseAllowanceTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_DECREASE_ALLOWANCE;

public final class DecreaseAllowanceTransactionDataRepository extends TransactionDataRepository<DecreaseAllowanceTransactionArguments> {
    DecreaseAllowanceTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.DECREASE_ALLOWANCE;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_DECREASE_ALLOWANCE,
            TRANSACTION_DECREASE_ALLOWANCE.TIMESTAMP,
            TRANSACTION_DECREASE_ALLOWANCE.SPENDER,
            TRANSACTION_DECREASE_ALLOWANCE.VALUE
        ).values((Long) null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, DecreaseAllowanceTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.spender.toBytes(),
            arguments.value
        );
    }
}
