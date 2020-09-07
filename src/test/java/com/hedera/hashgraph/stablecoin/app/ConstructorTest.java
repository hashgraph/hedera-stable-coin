package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class ConstructorTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void constructorTest() throws InvalidProtocolBufferException, SQLException {
        // prepare test transaction
        var callerKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var supplyManager = caller;
        var complianceManager = caller;
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = 2;
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
        Assertions.assertTrue(tokenDecimal >= 0);

        // iii. totalSupply >= 0
        Assertions.assertTrue(totalSupply.compareTo(BigInteger.ZERO) >= 0);

        // iv. caller != 0x
        Assertions.assertFalse(caller.isZero());

        // v. supplyManager != 0x
        Assertions.assertFalse(supplyManager.isZero());

        // vi. complianceManager != 0x
        Assertions.assertFalse(complianceManager.isZero());


        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));

        // Post-Check

        // i. TokenName = tokenName
        Assertions.assertEquals(tokenName, state.getTokenName());
        ;

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

        // vii. complianceManager = complianceManager
        Assertions.assertEquals(complianceManager, state.getComplianceManager());

        // viii. Balances = { SupplyManager->TotalSupply } // SupplyManager gets the TotalSupply of tokens
        Assertions.assertEquals(totalSupply, state.getBalanceOf(supplyManager));

        // ix. Allowances = {}
        Assertions.assertTrue(state.allowances.isEmpty());

        // x. Frozen = {} // no account is frozen by default
        Assertions.assertTrue(state.frozen.isEmpty());

        // xi. KycPassed = { Owner->true, SupplyManager->true ,complianceManager->true }
        Assertions.assertTrue(state.isKycPassed(state.getOwner()));
        Assertions.assertTrue(state.isKycPassed(state.getSupplyManager()));
        Assertions.assertTrue(state.isKycPassed(state.getComplianceManager()));

        // xii. ProposedOwner = 0x
        Assertions.assertEquals(Address.ZERO, state.getProposedOwner());
    }
}
