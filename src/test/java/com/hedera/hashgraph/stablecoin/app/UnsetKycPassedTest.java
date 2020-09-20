package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.UnsetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class UnsetKycPassedTest {
    State state = new State();
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void unsetKycPassedTest() throws InvalidProtocolBufferException, SQLException {
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

        var setKycPassedTransaction = new SetKycPassedTransaction(
            0,
            callerKey,
            addr
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        // prepare test transaction
        var unsetKycPassedTransaction = new UnsetKycPassedTransaction(
            0,
            callerKey,
            addr
        );

        var unsetKycPassedTransaction2 = new UnsetKycPassedTransaction(
            0,
            complianceManagerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. !isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. !KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));


        // re-set and check for caller == complianceManager instead this time
        var setKycPassedTransaction2 = new SetKycPassedTransaction(0, callerKey, addr);
        // Update State
        topicListener.handleTransaction(Instant.EPOCH.plusNanos(100), Transaction.parseFrom(setKycPassedTransaction2.toByteArray()));

        System.out.println(getTransactionStatus.status);

        Assertions.assertTrue(state.isKycPassed(addr));

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(complianceManager, state.getComplianceManager());

        // iii. !isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransaction2.toByteArray()));

        // Post-Check

        // i. !KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));


        // try to unset complianceManager, should fail
        var unsetKycPassedTransactionForCM = new UnsetKycPassedTransaction(
            0,
            callerKey,
            complianceManager
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(complianceManager, state.getComplianceManager());

        // iii. !isPrivilegedRole(addr)
        // Should be true as we expect to fail
        Assertions.assertTrue(state.isPrivilegedRole(complianceManager));

        // update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransactionForCM.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED, getTransactionStatus.status);
    }
}
