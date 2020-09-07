package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ChangeComplianceManagerTransactionArguments;
import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_CHANGE_COMPLIANCE_MANAGER;

public final class ChangeComplianceManagerTransactionDataRepository extends TransactionDataRepository<ChangeComplianceManagerTransactionArguments> {
    ChangeComplianceManagerTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.CHANGE_COMPLIANCE_MANAGER;
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_CHANGE_COMPLIANCE_MANAGER,
            TRANSACTION_CHANGE_COMPLIANCE_MANAGER.TIMESTAMP,
            TRANSACTION_CHANGE_COMPLIANCE_MANAGER.ADDRESS
        ).values((Long) null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ChangeComplianceManagerTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.address.toBytes()
        );
    }
}
