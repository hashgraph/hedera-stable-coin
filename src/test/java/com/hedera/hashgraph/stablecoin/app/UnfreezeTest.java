package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.FreezeTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.UnfreezeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class UnfreezeTest {
    State state = new State();
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void unfreezeTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var complianceManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var complianceManager = new Address(complianceManagerKey);
        var addr = new Address(addrKey);

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
            caller,
            complianceManager,
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(0, callerKey, addr);
        var freezeTransaction = new FreezeTransaction(0, callerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // prepare test transaction
        var unfreezeTransaction = new UnfreezeTransaction(
            0,
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransaction.toByteArray()));

        // Post-Check

        // i. complianceManager = addr
        Assertions.assertFalse(state.isFrozen(addr));


        // freeze and test for caller == complianceManager instead this time
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // prepare test transaction
        var unfreezeTransaction2 = new UnfreezeTransaction(
            0,
            complianceManagerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = complianceManager || caller = Owner
        Assertions.assertEquals(complianceManager, state.getComplianceManager());

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransaction2.toByteArray()));

        // Post-Check

        // i. complianceManager = addr
        Assertions.assertFalse(state.isFrozen(addr));


        // Try to unfreeze with addr, should fail
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // prepare test transaction
        var unfreezeTransactionForCM = new UnfreezeTransaction(
            addrKey,
            complianceManager
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = complianceManager || caller = Owner
        // Assert not equals as we expect to fail
        Assertions.assertNotEquals(addr, state.getOwner());

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransactionForCM.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.CALLER_NOT_AUTHORIZED, getTransactionStatus.status);
    }
}
