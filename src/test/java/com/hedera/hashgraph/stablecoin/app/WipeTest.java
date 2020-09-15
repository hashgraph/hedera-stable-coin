package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import com.hedera.hashgraph.stablecoin.sdk.WipeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class WipeTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void wipeTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var complianceManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var complianceManager = new Address(complianceManagerKey);
        var addr = new Address(addrKey);
        var value = BigInteger.ONE;

        // prepare state
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
            complianceManager
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, addr);
        var transferTransaction = new TransferTransaction(callerKey, addr, BigInteger.TWO);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));

        // prepare test transaction
        var wipeTransaction = new WipeTransaction(
            callerKey,
            addr,
            value
        );

        var balance = state.getBalanceOf(addr);

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iv. value <= Balances[addr]
        Assertions.assertTrue(value.compareTo(state.getBalanceOf(addr)) <= 0);

        // iv. value <= MAX_INT
        Assertions.assertTrue(value.compareTo(BigInteger.TWO.pow(256)) < 0);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(wipeTransaction.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply - value // total supply decreased
        Assertions.assertEquals(totalSupply.subtract(value), state.getTotalSupply());

        // ii. Balances[addr]’ = Balances[addr] - value // balance “updated”
        Assertions.assertEquals(balance.subtract(value), state.getBalanceOf(addr));

        // check for caller == complianceManager instead this time
        var totalSupply2 = state.getTotalSupply();
        var balance2 = state.getBalanceOf(addr);

        // prepare test transaction
        var wipeTransaction2 = new WipeTransaction(
            complianceManagerKey,
            addr,
            value
        );

        // Pre-Check

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iv. value <= Balances[addr]
        Assertions.assertTrue(value.compareTo(state.getBalanceOf(addr)) <= 0);

        // iv. value <= MAX_INT
        Assertions.assertTrue(value.compareTo(BigInteger.TWO.pow(256)) < 0);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(wipeTransaction2.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply - value // total supply decreased
        Assertions.assertEquals(totalSupply2.subtract(value), state.getTotalSupply());

        // ii. Balances[addr]’ = Balances[addr] - value // balance “updated”
        Assertions.assertEquals(balance2.subtract(value), state.getBalanceOf(addr));
    }
}
