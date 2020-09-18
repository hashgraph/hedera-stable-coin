package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ChangeComplianceManagerTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class ChangeComplianceManagerTest {
    State state = new State();
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void changecomplianceManagerTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        @Var var addr = new Address(addrKey);

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
            caller,
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(0, callerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var changecomplianceManagerTransaction = new ChangeComplianceManagerTransaction(
            0,
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. addr != 0x
        Assertions.assertFalse(addr.isZero());

        // iv. CheckTransferAllowed(addr)
        Assertions.assertTrue(state.checkTransferAllowed(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(changecomplianceManagerTransaction.toByteArray()));

        // Post-Check

        // i. complianceManager = addr
        Assertions.assertEquals(addr, state.getComplianceManager());


        // try with empty address, should fail
        addr = Address.ZERO;

        // prepare test transaction
        var changecomplianceManagerTransaction2 = new ChangeComplianceManagerTransaction(
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. addr != 0x
        // Assert true as it should fail
        Assertions.assertTrue(addr.isZero());

        // iv. CheckTransferAllowed(addr)
        // skip this one as it will be skipped in the handler anyways and the Status returned will be asserted below

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(changecomplianceManagerTransaction2.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.CHANGE_COMPLIANCE_MANAGER_ADDRESS_NOT_SET, getTransactionStatus.status);
    }
}
