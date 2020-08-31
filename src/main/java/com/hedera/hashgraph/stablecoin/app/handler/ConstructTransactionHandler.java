package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.app.State;
import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.app.handler.arguments.ConstructTransactionArguments;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;

public final class ConstructTransactionHandler extends TransactionHandler<ConstructTransactionArguments> {
    @Override
    public ConstructTransactionArguments parseArguments(TransactionBody transactionBody) {
        return new ConstructTransactionArguments(transactionBody);
    }

    @Override
    protected void validatePre(State state, Address caller, ConstructTransactionArguments args) throws StableCoinPreCheckException {
        // i. Owner = 0x
        ensure(state.getOwner().isZero(), Status.CONSTRUCTOR_OWNER_ALREADY_SET);

        // ii. tokenDecimal >= 0
        ensureZeroOrGreater(args.tokenDecimal, Status.CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO);

        // iii. totalSupply >= 0
        ensureZeroOrGreater(args.totalSupply, Status.CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO);

        // iv. caller != 0x
        // NOTE: checked in TopicListener#handleTransaction for *all* transactions

        // v. supplyManager != 0x
        ensure(!args.supplyManager.isZero(), Status.CONSTRUCTOR_SUPPLY_MANAGER_NOT_SET);

        // vi. assetProtectionManager != 0x
        ensure(!args.assetProtectionManager.isZero(), Status.CONSTRUCTOR_ASSET_PROTECTION_MANAGER_NOT_SET);

        // vii. tokenDecimal <= MAX_INT
        ensureLessThanMaxInt(args.tokenDecimal, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);

        // viii. totalSupply <= MAX_INT
        ensureLessThanMaxInt(args.totalSupply, Status.NUMBER_VALUES_LIMITED_TO_256_BITS);
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
        state.increaseTotalSupply(args.totalSupply);

        // v. Owner = caller
        state.setOwner(caller);

        // vi. SupplyManager = supplyManager
        state.setSupplyManager(args.supplyManager);

        // vii. AssetProtectionManager = assetProtectionManager
        state.setAssetProtectionManager(args.assetProtectionManager);

        // viii. Balances = { SupplyManager->TotalSupply } (SupplyManager gets the TotalSupply of tokens)
        state.increaseBalanceOf(args.supplyManager, args.totalSupply);

        // ix. Allowances = {}
        // NOTE: maps are default initialized in State

        // x. Frozen = {}
        // NOTE: maps are default initialized in state

        // xi. KycPassed = { Owner->true, SupplyManager->true ,AssetProtectionManager->true }
        state.setKycPassed(caller);
        state.setKycPassed(args.supplyManager);
        state.setKycPassed(args.assetProtectionManager);

        // xii. ProposedOwner = 0x
        // NOTE: this is default initialized in State
    }
}
