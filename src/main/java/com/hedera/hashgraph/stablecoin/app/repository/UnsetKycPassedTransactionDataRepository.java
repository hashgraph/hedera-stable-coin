package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.UnsetKycPassedTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_UNSET_KYC_PASSED;

public final class UnsetKycPassedTransactionDataRepository extends TransactionDataRepository<UnsetKycPassedTransactionArguments> {
    UnsetKycPassedTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.UNSET_KYC_PASSED;
    }

    @Override
    public Collection<Address> getAddressList(UnsetKycPassedTransactionArguments arguments) {
        return Collections.singletonList(arguments.address);
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_UNSET_KYC_PASSED,
            TRANSACTION_UNSET_KYC_PASSED.TIMESTAMP,
            TRANSACTION_UNSET_KYC_PASSED.ADDRESS
        ).values((Long) null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, UnsetKycPassedTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.address.toBytes()
        );
    }
}
