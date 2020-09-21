package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.api.TransactionResponseItem;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import io.vertx.core.json.JsonObject;
import org.jooq.BatchBindStep;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;

public abstract class TransactionDataRepository<ArgumentsT> {
    protected SqlConnectionManager connectionManager;

    @Nullable
    private BatchBindStep batch;

    protected TransactionDataRepository(SqlConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public abstract BatchBindStep newBatch() throws SQLException;

    public abstract TransactionKind getTransactionKind();

    public TransactionResponseItem toTransactionResponseItem(Instant consensusTimestamp, Status status, Address caller, ArgumentsT arguments) {
        return new TransactionResponseItem(consensusTimestamp, status, getTransactionKind().getName(), caller, toTransactionData(arguments));
    }

    public abstract JsonObject toTransactionData(ArgumentsT arguments);

    public abstract Collection<Address> getAddressList(ArgumentsT arguments);

    public synchronized void bindTransaction(Instant consensusTimestamp, ArgumentsT arguments) throws SQLException {
        if (batch == null) {
            batch = newBatch();
        }

        batch = bindArguments(batch, consensusTimestamp, arguments);
    }

    public synchronized void execute() {
        if (batch == null) {
            return;
        }

        batch.execute();
        batch = null;
    }

    protected abstract BatchBindStep bindArguments(BatchBindStep batch, Instant consensusTimestamp, ArgumentsT arguments);
}
