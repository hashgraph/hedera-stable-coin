package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class ConstructorTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void constructorTest() throws InvalidProtocolBufferException {
        // prepare test transaction
        var callerKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var supplyManager = caller;
        var assetProtectionManager= caller;
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = new BigInteger("2");
        var totalSupply = new BigInteger("10000");

        var constructTransaction = new ConstructTransaction(
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            caller,
            caller
        );

        // Pre-Check

        // i. Owner = 0x
        Assertions.assertTrue(state.getOwner().isZero());

        // ii. tokenDecimal >= 0
        Assertions.assertTrue(tokenDecimal.compareTo(BigInteger.ZERO) >= 0);

        // iii. totalSupply >= 0
        Assertions.assertTrue(totalSupply.compareTo(BigInteger.ZERO) >= 0);

        // iv. caller != 0x
        Assertions.assertFalse(caller.isZero());

        // v. supplyManager != 0x
        Assertions.assertFalse(supplyManager.isZero());

        // vi. assetProtectionManager != 0x
        Assertions.assertFalse(assetProtectionManager.isZero());


        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));

        // Post-Check

        // i. TokenName = tokenName
        Assertions.assertEquals(tokenName, state.getTokenName()); ;

        // ii. TokenSymbol = tokenSymbol
        Assertions.assertEquals(tokenSymbol, state.getTokenSymbol());

        // iii. TokenDecimal = tokenDecimal
        Assertions.assertEquals(tokenDecimal, state.getTokenDecimal());

        // iv. TotalSupply = totalSupply
        Assertions.assertEquals(totalSupply, state.getTotalSupply());

        // v. Owner = caller
        Assertions.assertEquals(caller, state.getOwner());

        // vi. SupplyManager = supplyManager
        Assertions.assertEquals(supplyManager, state.getSupplyManager());

        // vii. AssetProtectionManager = assetProtectionManager
        Assertions.assertEquals(assetProtectionManager, state.getAssetProtectionManager());

        // viii. Balances = { SupplyManager->TotalSupply } // SupplyManager gets the TotalSupply of tokens
        Assertions.assertEquals(totalSupply, state.getBalanceOf(supplyManager));

        // ix. Allowances = {}
        Assertions.assertTrue(state.isAllowancesEmpty());

        // x. Frozen = {} // no account is frozen by default
        Assertions.assertTrue(state.isFrozenEmpty());

        // xi. KycPassed = { Owner->true, SupplyManager->true ,AssetProtectionManager->true }
        Assertions.assertTrue(state.getKycPassed(state.getOwner()));
        Assertions.assertTrue(state.getKycPassed(state.getSupplyManager()));
        Assertions.assertTrue(state.getKycPassed(state.getAssetProtectionManager()));

        // xii. ProposedOwner = 0x
        Assertions.assertEquals(Address.ZERO, state.getProposedOwner());
    }
}
