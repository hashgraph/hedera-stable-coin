package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ExternalTransferFromTransactionArguments;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import org.jooq.BatchBindStep;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION_EXTERNAL_TRANSFER_FROM;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class ExternalTransferFromTransactionDataRepository extends TransactionDataRepository<ExternalTransferFromTransactionArguments> {
    ExternalTransferFromTransactionDataRepository(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public TransactionKind getTransactionKind() {
        return TransactionKind.EXTERNAL_TRANSFER_FROM;
    }

    @Override
    public Collection<Address> getAddressList(ExternalTransferFromTransactionArguments arguments) {
        return Collections.singletonList(arguments.to);
    }

    @Override
    public BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION_EXTERNAL_TRANSFER_FROM,
            TRANSACTION_EXTERNAL_TRANSFER_FROM.TIMESTAMP,
            TRANSACTION_EXTERNAL_TRANSFER_FROM.ADDRESS_FROM,
            TRANSACTION_EXTERNAL_TRANSFER_FROM.NETWORK_URI,
            TRANSACTION_EXTERNAL_TRANSFER_FROM.ADDRESS_TO,
            TRANSACTION_EXTERNAL_TRANSFER_FROM.AMOUNT
        ).values((Long) null, null, null, null, null).onConflictDoNothing());
    }

    @Override
    public BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ExternalTransferFromTransactionArguments arguments) {
        return batch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            arguments.from.toStringUtf8().getBytes(UTF_8),
            arguments.networkURI,
            arguments.to.toBytes(),
            arguments.amount
        );
    }
}
