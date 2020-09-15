package com.hedera.hashgraph.stablecoin.app.emitter;

import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

import java.util.Map;

public class Emitter {
    private static final Map<TransactionBody.DataCase, AbstractEmitter<?>> emitters = Map.ofEntries(
        Map.entry(TransactionBody.DataCase.CONSTRUCT, new ConstructEmitter()),
        Map.entry(TransactionBody.DataCase.APPROVE, new ApproveAllowanceEmitter()),
        Map.entry(TransactionBody.DataCase.MINT, new MintEmitter()),
        Map.entry(TransactionBody.DataCase.BURN, new BurnEmitter()),
        Map.entry(TransactionBody.DataCase.TRANSFER, new TransferEmitter()),
        Map.entry(TransactionBody.DataCase.TRANSFERFROM, new TransferFromEmitter()),
        Map.entry(TransactionBody.DataCase.PROPOSEOWNER, new ProposeOwnerEmitter()),
        Map.entry(TransactionBody.DataCase.CLAIMOWNERSHIP, new ClaimOwnershipEmitter()),
        Map.entry(TransactionBody.DataCase.CHANGESUPPLYMANAGER, new ChangeSupplyManagerEmitter()),
        Map.entry(TransactionBody.DataCase.CHANGECOMPLIANCEMANAGER, new ChangeComplianceManagerEmitter()),
        Map.entry(TransactionBody.DataCase.CHANGEENFORCEMENTMANAGER, new ChangeEnforcementManagerEmitter()),
        Map.entry(TransactionBody.DataCase.FREEZE, new FreezeEmitter()),
        Map.entry(TransactionBody.DataCase.UNFREEZE, new UnfreezeEmitter()),
        Map.entry(TransactionBody.DataCase.WIPE, new WipeEmitter()),
        Map.entry(TransactionBody.DataCase.SETKYCPASSED, new SetKycPassedEmitter()),
        Map.entry(TransactionBody.DataCase.UNSETKYCPASSED, new UnsetKycPassedEmitter()),
        Map.entry(TransactionBody.DataCase.INCREASEALLOWANCE, new IncreaseAllowanceEmitter()),
        Map.entry(TransactionBody.DataCase.DECREASEALLOWANCE, new DecreaseAllowanceEmitter()),
        Map.entry(TransactionBody.DataCase.APPROVEEXTERNALTRANSFER, new ApproveExternalTransferEmitter()),
        Map.entry(TransactionBody.DataCase.EXTERNALTRANSFER, new ExternalTransferEmitter())
    );

    public <ArgumentsT> void emit(TransactionBody.DataCase dataCase, State state, TransactionId transactionId, ArgumentsT arguments) {
        @SuppressWarnings("unchecked")
        var emitter = (AbstractEmitter<ArgumentsT>) emitters.get(dataCase);

        if (emitter == null) {
            // no registered emitter for this transaction
            return;
        }

        emitter.emit(state, transactionId, arguments);
    }
}
