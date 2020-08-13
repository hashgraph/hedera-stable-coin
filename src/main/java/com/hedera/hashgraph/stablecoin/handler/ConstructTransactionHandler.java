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
        ensure(!caller.isZero(), Status.CALLER_ZERO);

        // v. supplyManager != 0x
        ensure(!args.supplyManager.isZero(), Status.CONSTRUCTOR_SUPPLY_MANAGER_ZERO);

        // vi. assetProtectionManager != 0x
        ensure(!args.assetProtectionManager.isZero(), Status.CONSTRUCTOR_ASSET_PROTECTION_MANAGER_ZERO);
    }

    @Override
    protected void validatePost(State state, Address caller, ConstructTransactionArguments args) {
        // i. TokenName = tokenName
        assert state.getTokenName().equals(args.tokenName);

        // ii. TokenSymbol = tokenSymbol
        assert state.getTokenSymbol().equals(args.tokenSymbol);

        // iii. TokenDecimal = tokenDecimal
        assert state.getTokenDecimal().equals(args.tokenDecimal);

        // iv. TotalSupply = totalSupply
        assert state.getTotalSupply().equals(args.totalSupply);

        // v. Owner = caller
        assert state.getOwner().equals(caller);

        // vi. SupplyManager = supplyManager
        assert state.getSupplyManager().equals(args.supplyManager);

        // vii. AssetProtectionManager = assetProtectionManager
        assert state.getAssetProtectionManager().equals(args.assetProtectionManager);

        // viii. Balances = { SupplyManager->TotalSupply } // SupplyManager gets theTotalSupply of tokens
        assert state.getBalanceOf(state.getSupplyManager()).equals(args.totalSupply);

        // ix. Allowances = {}
        assert state.isAllowancesEmpty();

        // x. Frozen = {} // no account is frozen by default
        assert state.isFrozenEmpty();

        // xi. KycPassed = { Owner->true, SupplyManager->true,AssetProtectionManager->true }
        assert state.isKycPassed(state.getOwner());
        assert state.isKycPassed(state.getSupplyManager());
        assert state.isKycPassed(state.getAssetProtectionManager());

        // xii. ProposedOwner = 0x
        assert state.getProposedOwner().equals(Address.ZERO);
    }

    @Override
    protected void updateState(State state, Address caller, ConstructTransactionArguments args) {
        state.setTokenName(args.tokenName);
        state.setTokenSymbol(args.tokenSymbol);
        state.setTokenDecimal(args.tokenDecimal);
        state.setTotalSupply(args.totalSupply);
        state.setOwner(caller);
        state.setSupplyManager(args.supplyManager);
        state.setAssetProtectionManager(args.assetProtectionManager);
        state.increaseBalanceOf(args.supplyManager, args.totalSupply);
        state.setKycPassed(caller, true);
        state.setKycPassed(args.supplyManager, true);
        state.setKycPassed(args.assetProtectionManager, true);
    }
}
