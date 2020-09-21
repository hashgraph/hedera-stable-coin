package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.api.TransactionResponseItem;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import org.jooq.BatchBindStep;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hedera.hashgraph.stablecoin.app.db.Tables.ADDRESS_TRANSACTION;
import static com.hedera.hashgraph.stablecoin.app.db.Tables.TRANSACTION;

public class TransactionRepository {
    private final SqlConnectionManager connectionManager;

    private final Map<TransactionBody.DataCase, TransactionDataRepository<?>> transactionDataBatch;

    private final Set<TransactionBody.DataCase> transactionsWithData = new HashSet<>();

    private final List<TransactionResponseItem> pendingTransactions = new ArrayList<>();

    private final Map<Address, List<TransactionResponseItem>> pendingTransactionsByAddress = new HashMap<>();

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

    private static List<TransactionResponseItem> boundUnmodifiableList(List<TransactionResponseItem> list, int limit) {
        if (list.size() <= limit) {
            return Collections.unmodifiableList(list);
        }

        return Collections.unmodifiableList(list.subList(0, limit));
    }

    private BatchBindStep newBatch() throws SQLException {
        var cx = connectionManager.dsl();

        return cx.batch(cx.insertInto(TRANSACTION,
            TRANSACTION.TIMESTAMP,
            TRANSACTION.KIND,
            TRANSACTION.CALLER,
            TRANSACTION.OPERATOR_ACCOUNT_NUM,
            TRANSACTION.VALID_START_NANOS,
            TRANSACTION.STATUS
        ).values((Long) null, null, null, null, null, null).onConflictDoNothing());
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
        Address caller,
        TransactionId transactionId,
        Status status,
        TransactionBody.DataCase dataCase,
        ArgumentsT arguments
    ) throws SQLException {
        if (transactionBatch == null) {
            transactionBatch = newBatch();
        }

        if (addressBatch == null) {
            addressBatch = newAddressBatch();
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
        assert addressBatch != null;

        var transactionTimestamp = ChronoUnit.NANOS.between(Instant.EPOCH, consensusTimestamp);

        transactionBatch = transactionBatch.bind(
            transactionTimestamp,
            transactionKind,
            caller.toBytes(),
            transactionId.accountId.account,
            ChronoUnit.NANOS.between(Instant.EPOCH, transactionId.validStart),
            status.getValue()
        );

        addressBatch = addressBatch.bind(transactionTimestamp, caller.toBytes());

        if (repository != null) {
            var pendingTransaction = repository.toTransactionResponseItem(
                consensusTimestamp, status, caller, arguments);

            pendingTransactions.add(pendingTransaction);
            repository.bindTransaction(consensusTimestamp, arguments);

            for (var address : repository.getAddressList(arguments)) {
                pendingTransactionsByAddress.computeIfAbsent(address, k -> new ArrayList<>()).add(pendingTransaction);
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
        pendingTransactions.clear();
        pendingTransactionsByAddress.clear();
    }

    public List<TransactionResponseItem> getPendingTransactions(int limit) {
        return boundUnmodifiableList(pendingTransactions, limit);
    }

    public List<TransactionResponseItem> getPendingTransactionsFor(Address address, int limit) {
        var pendingTransactions = pendingTransactionsByAddress.get(address);

        if (pendingTransactions == null) {
            return Collections.emptyList();
        }

        return boundUnmodifiableList(pendingTransactions, limit);
    }
}
