package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
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
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void burnTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var supplyManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey.publicKey);
        var supplyManager = new Address(supplyManagerKey.publicKey);
        var addr = new Address(addrKey.publicKey);
        @Var var value = BigInteger.ONE;

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
            caller,
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


        // Try to burn with caller != SupplyManager && caller != Owner
        // prepare test transaction
        var burnTransactionAsAddr = new BurnTransaction(
            addrKey,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = SupplyManager || caller = Owner
        // Assert NotEquals as we expect to fail
        Assertions.assertNotEquals(addr, state.getOwner());
        Assertions.assertNotEquals(addr, state.getSupplyManager());

        // iii. value >= 0
        // can skip, handler won't hit this check

        // iv. Balances[SupplyManager] >= value
        // Assert false as we expect to fail here
        // can skip, handler won't hit this check

        // v. TotalSupply >= Balances[SupplyManager]
        // can skip, handler won't hit this check

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(burnTransactionAsAddr.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.CALLER_NOT_AUTHORIZED, getTransactionStatus.status);


        // Test for Balances[SupplyManager] < value
        // prepare test transaction
        value = new BigInteger("10020");
        var burnTransaction2 = new BurnTransaction(
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
        // Assert false as we expect to fail here
        Assertions.assertFalse(state.getBalanceOf(state.getSupplyManager()).compareTo(value) >= 0);

        // v. TotalSupply >= Balances[SupplyManager]
        // can skip, handler won't hit this check

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(burnTransaction2.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.BURN_INSUFFICIENT_SUPPLY_MANAGER_BALANCE, getTransactionStatus.status);
    }
}
