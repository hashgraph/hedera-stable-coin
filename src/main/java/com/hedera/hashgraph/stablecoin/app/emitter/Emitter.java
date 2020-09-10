package com.hedera.hashgraph.stablecoin.app.emitter;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import java.util.Map;

public class Emitter {
    private static final Map<TransactionBody.DataCase, AbstractEmitter<?>> emitters = Map.ofEntries(
        Map.entry(TransactionBody.DataCase.APPROVEEXTERNALTRANSFER, new ApproveExternalTransferEmitter())
    );

    public <ArgumentsT> void emit(TransactionBody.DataCase dataCase, State state, Address caller, ArgumentsT arguments) {
        @SuppressWarnings("unchecked")
        var emitter = (AbstractEmitter<ArgumentsT>) emitters.get(dataCase);

        if (emitter == null) {
            // no registered emitter for this transaction
            return;
        }

        emitter.emit(state, caller, arguments);
    }
}
