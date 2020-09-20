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

public class FreezeTest {
    State state = new State();
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void freezeTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var complianceManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var addr2Key = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var complianceManager = new Address(complianceManagerKey);
        var addr = new Address(addrKey);
        var addr2 = new Address(addr2Key);

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

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var freezeTransaction = new FreezeTransaction(
            0,
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii.!isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-Check

        // i. complianceManager = addr
        Assertions.assertTrue(state.isFrozen(addr));


        // unfreeze and check for caller == complianceManager instead this time
        var unfreezeTransaction = new UnfreezeTransaction(0, callerKey, addr);
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransaction.toByteArray()));

        // prepare test transaction
        var freezeTransaction2 = new FreezeTransaction(
            0,
            complianceManagerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = complianceManager || caller = Owner
        Assertions.assertEquals(complianceManager, state.getComplianceManager());

        // iii.!isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction2.toByteArray()));

        // Post-Check

        // i. complianceManager = addr
        Assertions.assertTrue(state.isFrozen(addr));


        // Try to freeze complianceManager, should fail
        // prepare test transaction
        var freezeTransactionForCM = new FreezeTransaction(
            0,
            callerKey,
            complianceManager
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii.!isPrivilegedRole(addr)
        // Should be true as we expect to fail
        Assertions.assertTrue(state.isPrivilegedRole(complianceManager));

        // Update State and check status
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransactionForCM.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.FREEZE_ADDRESS_IS_PRIVILEGED, getTransactionStatus.status);


        // Try to freeze with caller != complianceManager && caller != Owner, should fail
        // prepare test transaction
        var freezeTransactionAsAddr = new FreezeTransaction(
            0,
            addrKey,
            addr2
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = complianceManager || caller = Owner
        Assertions.assertNotEquals(addr, state.getOwner());
        Assertions.assertNotEquals(addr, state.getComplianceManager());

        // iii.!isPrivilegedRole(addr)
        // can skip, handler won't hit this check

        // Update State and check status
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransactionAsAddr.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.CALLER_NOT_AUTHORIZED, getTransactionStatus.status);
    }
}
