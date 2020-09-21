package com.hedera.hashgraph.stablecoin.app.repository;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.stablecoin.app.SqlConnectionManager;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.time.Instant;

public class GetTransactionStatus extends TransactionRepository {
    public Status status;

    @SuppressWarnings("NullAway")
    public GetTransactionStatus(SqlConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override public synchronized <ArgumentsT> void bindTransaction(
        Instant consensusTimestamp,
        Address caller,
        TransactionId transactionId,
        Status status,
        TransactionBody.DataCase dataCase,
        ArgumentsT arguments
    ) {
        if (status != null) {
            this.status = status;
        }
    }
}
