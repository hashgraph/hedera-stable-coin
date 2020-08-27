package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import org.jooq.BatchBindStep;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION;

public final class TransactionRepository {
    private final SqlConnectionManager connectionManager;

    private final Map<TransactionBody.DataCase, TransactionDataRepository<?>> transactionDataBatch;

    private final Set<TransactionBody.DataCase> transactionsWithData = new HashSet<>();

    @Nullable
    private BatchBindStep transactionBatch;

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
            // Map.entry(TransactionBody.DataCase.CLAIMOWNERSHIP, null),
            Map.entry(TransactionBody.DataCase.CHANGESUPPLYMANAGER, new ChangeSupplyManagerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.CHANGEASSETPROTECTIONMANAGER, new ChangeAssetProtectionManagerTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.FREEZE, new FreezeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.UNFREEZE, new UnfreezeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.WIPE, new WipeTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.SETKYCPASSED, new SetKycPassedTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.UNSETKYCPASSED, new UnsetKycPassedTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.INCREASEALLOWANCE, new IncreaseAllowanceTransactionDataRepository(connectionManager)),
            Map.entry(TransactionBody.DataCase.DECREASEALLOWANCE, new DecreaseAllowanceTransactionDataRepository(connectionManager))
        );
    }

    private BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION,
            TRANSACTION.TIMESTAMP,
            TRANSACTION.KIND,
            TRANSACTION.CALLER,
            TRANSACTION.STATUS
        ).values((Long) null, null, null, null));
    }

    public synchronized <ArgumentsT> void bindTransaction(
        Instant consensusTimestamp,
        Address caller,
        Status status,
        TransactionBody.DataCase dataCase,
        ArgumentsT arguments
    ) throws SQLException {
        if (transactionBatch == null) {
            transactionBatch = newBatch();
        }

        @SuppressWarnings("unchecked")
        var repository = (TransactionDataRepository<ArgumentsT>) transactionDataBatch.get(dataCase);

        // <claim ownership> does not have any associated data
        var transactionKind = dataCase == TransactionBody.DataCase.CLAIMOWNERSHIP
            ? TransactionKind.CLAIM_OWNERSHIP : repository.getTransactionKind();

        // this is a <synchronized> method and `transactionBatch` should not
        // have became null since the start of the method
        assert transactionBatch != null;

        transactionBatch = transactionBatch.bind(
            ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp),
            transactionKind,
            caller.toBytes(),
            status.getValue()
        );

        if (repository != null) {
            repository.bindTransaction(consensusTimestamp, arguments);

            transactionsWithData.add(dataCase);
        }
    }

    public synchronized void execute() {
        if (transactionBatch == null) {
            return;
        }

        transactionBatch.execute();
        transactionBatch = null;

        for (var dataCase : transactionsWithData) {
            var repository = transactionDataBatch.get(dataCase);
            assert repository != null;

            repository.execute();
        }

        transactionsWithData.clear();
    }
}
