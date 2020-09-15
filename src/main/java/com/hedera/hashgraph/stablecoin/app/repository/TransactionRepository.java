package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

import org.jooq.Batch;
import org.jooq.BatchBindStep;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION;
import static com.hedera.hashgraph.stablecoin.app.db.Tables.ADDRESS_TRANSACTION;

public final class TransactionRepository {
    private final SqlConnectionManager connectionManager;

    private final Map<TransactionBody.DataCase, TransactionDataRepository<?>> transactionDataBatch;

    private final Set<TransactionBody.DataCase> transactionsWithData = new HashSet<>();

    @Nullable
    private BatchBindStep transactionBatch;

    @Nullable
    private BatchBindStep addressBatch;

    public TransactionRepository(SqlConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.transactionDataBatch = Map.ofEntries(
            Map.entry(TransactionBody.DataCase.CONSTRUCT, new ConstructTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.APPROVE, new ApproveAllowanceTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.MINT, new MintTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.BURN, new BurnTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.TRANSFER, new TransferTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.TRANSFERFROM, new TransferFromTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.PROPOSEOWNER, new ProposeOwnerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.CHANGESUPPLYMANAGER, new ChangeSupplyManagerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.CHANGECOMPLIANCEMANAGER, new ChangeComplianceManagerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.CHANGEENFORCEMENTMANAGER, new ChangeEnforcementManagerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.FREEZE, new FreezeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.UNFREEZE, new UnfreezeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.WIPE, new WipeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.SETKYCPASSED, new SetKycPassedTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.UNSETKYCPASSED, new UnsetKycPassedTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.INCREASEALLOWANCE, new IncreaseAllowanceTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.DECREASEALLOWANCE, new DecreaseAllowanceTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.APPROVEEXTERNALTRANSFER, new ApproveExternalTransferTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.EXTERNALTRANSFER, new ExternalTransferTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.EXTERNALTRANSFERFROM, new ExternalTransferFromTransactionDataRepository(connectionManager))
        );
    }

    private BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION,
            TRANSACTION.TIMESTAMP,
            TRANSACTION.KIND,
            TRANSACTION.CALLER,
            TRANSACTION.VALID_START,
            TRANSACTION.STATUS
        ).values((Long) null, null, null, null, null).onConflictDoNothing());
    }

    private BatchBindStep newAddressBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(ADDRESS_TRANSACTION,
            ADDRESS_TRANSACTION.TRANSACTION_TIMESTAMP,
            ADDRESS_TRANSACTION.ADDRESS
        ).values((Long) null, null).onConflictDoNothing());
    }

    public synchronized <ArgumentsT> void bindTransaction(
        Instant consensusTimestamp,
        TransactionId transactionId,
        Status status,
        TransactionBody.DataCase dataCase,
        ArgumentsT arguments
    ) throws SQLException {
        if (transactionBatch == null) {
            transactionBatch = newBatch();
        }

        if (addressBatch == null) {
            addressBatch = newBatch();
        }

        @SuppressWarnings("unchecked")
        var repository = (TransactionDataRepository<ArgumentsT>) transactionDataBatch.get(dataCase);

        // <claim ownership> does not have any associated data
        @SuppressWarnings("NullAway")
        var transactionKind = (dataCase == TransactionBody.DataCase.CLAIMOWNERSHIP
            ? TransactionKind.CLAIM_OWNERSHIP : repository.getTransactionKind()).getValue();

        // this is a <synchronized> method and `transactionBatch` should not
        // have became null since the start of the method
        assert transactionBatch != null;

        var transactionTimestamp = ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp);

        transactionBatch = transactionBatch.bind(
            transactionTimestamp,
            transactionKind,
            transactionId.address.toBytes(),
            ChronoUnit.NANOS.between(Instant.EPOCH, transactionId.validStart),
            status.getValue()
        );

        if (repository != null) {
            repository.bindTransaction(consensusTimestamp, arguments);

            for (var address : repository.getAddressList(arguments)) {
                addressBatch = addressBatch.bind(transactionTimestamp, address.toBytes());
            }

            transactionsWithData.add(dataCase);
        }
    }

    public synchronized void execute() {
        if (transactionBatch != null) {
            transactionBatch.execute();
            transactionBatch = null;
        }

        if (addressBatch != null) {
            addressBatch.execute();
            addressBatch = null;
        }

        for (var dataCase : transactionsWithData) {
            var repository = transactionDataBatch.get(dataCase);

            if (repository != null) {
                repository.execute();
            }
        }

        transactionsWithData.clear();
    }
}
