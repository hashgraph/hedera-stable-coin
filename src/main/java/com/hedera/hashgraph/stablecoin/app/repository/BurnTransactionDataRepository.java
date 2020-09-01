package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.BurnTransactionArguments;
import org.jooq.BatchBindStep;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_BURN;

public final class BurnTransactionDataRepository extends TransactionDataRepository<BurnTransactionArguments> {
    BurnTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.BURN;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_BURN,
            TRANSACTION_BURN.TIMESTAMP,
            TRANSACTION_BURN.VALUE
        ).values(null, (BigInteger) null));
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, BurnTransactionArguments arguments) {
        return batch.bind(ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp), arguments.value);
    }
}