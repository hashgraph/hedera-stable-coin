package com.hedera.hashgraph.stablecoin.handler;

import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.State;
import com.hedera.hashgraph.stablecoin.Status;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.handler.arguments.ConstructTransactionArguments;

import java.math.BigInteger;

public final class ConstructTransactionHandler extends TransactionHandler<ConstructTransactionArguments> {
    @Override
    protected ConstructTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ConstructTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ConstructTransactionArguments args) {
        // i. Owner = 0x
        ensure(!state.hasOwner(), Status.CONSTRUCTOR_OWNER_ALREADY_SET);

        // ii. tokenDecimal >= 0
        ensure(args.tokenDecimal.compareTo(BigInteger.ZERO) >= 0, Status.CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO);

        // iii. totalSupply >= 0
        ensure(args.totalSupply.compareTo(BigInteger.ZERO) >= 0, Status.CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO);

        // iv. caller != 0x
        ensure(!caller.isZero(), Status.CONSTRUCTOR_CALLER_ZERO);

        // v. supplyManager != 0x
        ensure(!args.supplyManager.isZero(), Status.CONSTRUCTOR_SUPPLY_MANAGER_ZERO);

        // vi. assetProtectionManager != 0x
        ensure(!args.assetProtectionManager.isZero(), Status.CONSTRUCTOR_ASSET_PROTECTION_MANAGER_ZERO);
    }

    @Override
    protected void updateState(State state, Address caller, ConstructTransactionArguments args) {
        // i. TokenName = tokenName
        state.setTokenName(args.tokenName);

        // ii. TokenSymbol = tokenSymbol
        state.setTokenSymbol(args.tokenSymbol);

        // iii. TokenDecimal = tokenDecimal
        state.setTokenDecimal(args.tokenDecimal);

        // iv. TotalSupply = totalSupply
        state.setTotalSupply(args.totalSupply);

        // v. Owner = caller
        state.setOwner(caller);

        // vi. SupplyManager = supplyManager
        state.setSupplyManager(args.supplyManager);

        // vii. AssetProtectionManager = assetProtectionManager
        state.setAssetProtectionManager(args.assetProtectionManager);

        // viii. Balances = { SupplyManager->TotalSupply } // SupplyManager gets the TotalSupply of tokens
        state.increaseBalanceOf(state.getSupplyManager(), state.getTotalSupply());
        
        // ix. Allowances = {}
        // This happens by default. `State.allowances` is an empty map already.

        // x. Frozen = {} // no account is frozen by default
        // This happens by default. `State.frozen` is an empty map already.

        // xi. KycPassed = { Owner->true, SupplyManager->true ,AssetProtectionManager->true }
        state.setKycPassed(state.getOwner(), true);
        state.setKycPassed(state.getSupplyManager(), true);
        state.setKycPassed(state.getAssetProtectionManager(), true);

        // xii. ProposedOwner = 0x
        // This happens by default. `State.proposedOwner` is the empty address already.
    }
}
