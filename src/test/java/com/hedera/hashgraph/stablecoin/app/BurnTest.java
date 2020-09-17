package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.BurnTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class BurnTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void burnTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var supplyManagerKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey.publicKey);
        var supplyManager = new Address(supplyManagerKey.publicKey);
        var value = BigInteger.ONE;

        // prepare state
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = 2;
        var totalSupply = new BigInteger("10000");

        var constructTransaction = new ConstructTransaction(
            0,
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            supplyManager,
            caller
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));

        // prepare test transaction
        var burnTransaction = new BurnTransaction(
            0,
            callerKey,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = SupplyManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iv. Balances[SupplyManager] >= value
        Assertions.assertTrue(state.getBalanceOf(state.getSupplyManager()).compareTo(value) >= 0);

        // v. TotalSupply >= Balances[SupplyManager]
        Assertions.assertTrue(state.getTotalSupply().compareTo(state.getBalanceOf(supplyManager)) >= 0);

        // Prepare Post-check
        var supplyManagerBalance = state.getBalanceOf(supplyManager);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(burnTransaction.toByteArray()));

        // Post-Check

        // i.TotalSupply’ = TotalSupply - value // the new supply is decreased byvalue
        Assertions.assertEquals(totalSupply.subtract(value), state.getTotalSupply());

        // ii.Balances[SupplyManager]’ = Balances[SupplyManager] - value
        Assertions.assertEquals(supplyManagerBalance.subtract(value), state.getBalanceOf(supplyManager));
    }
}
