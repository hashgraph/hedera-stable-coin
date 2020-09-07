package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ConstructTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_CONSTRUCT;

public final class ConstructTransactionDataRepository extends TransactionDataRepository<ConstructTransactionArguments> {
    ConstructTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.CONSTRUCT;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_CONSTRUCT,
            TRANSACTION_CONSTRUCT.TIMESTAMP,
            TRANSACTION_CONSTRUCT.TOKEN_NAME,
            TRANSACTION_CONSTRUCT.TOKEN_SYMBOL,
            TRANSACTION_CONSTRUCT.TOKEN_DECIMAL,
            TRANSACTION_CONSTRUCT.TOTAL_SUPPLY,
            TRANSACTION_CONSTRUCT.COMPLIANCE_MANAGER,
            TRANSACTION_CONSTRUCT.SUPPLY_MANAGER
        ).values((Long) null, null, null, null, null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ConstructTransactionArguments arguments) {
        return batch.bind(ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.tokenName,
            arguments.tokenSymbol,
            arguments.tokenDecimal,
            arguments.totalSupply,
            arguments.complianceManager.toBytes(),
            arguments.supplyManager.toBytes());
    }
}
