package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.MintTransactionArguments;
import org.jooq.BatchBindStep;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_MINT;

public final class MintTransactionDataRepository extends TransactionDataRepository<MintTransactionArguments> {
    MintTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.MINT;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_MINT,
            TRANSACTION_MINT.TIMESTAMP,
            TRANSACTION_MINT.VALUE
        ).values(null, (BigInteger) null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, MintTransactionArguments arguments) {
        return batch.bind(ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp), arguments.value);
    }
}
