package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
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
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void unsetKycPassedTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var assetManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var assetManager = new Address(assetManagerKey);
        var addr = new Address(addrKey);

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
            assetManager
        );

        var setKycPassedTransaction = new SetKycPassedTransaction(
            callerKey,
            addr
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        // prepare test transaction
        var unsetKycPassedTransaction = new UnsetKycPassedTransaction(
            callerKey,
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
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        Assertions.assertTrue(state.isKycPassed(addr));

        var unsetKycPassedTransaction2 = new SetKycPassedTransaction(
            assetManagerKey,
            addr
        );

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransaction2.toByteArray()));

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(assetManager, state.getComplianceManager());

        // iii. !isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. !KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));
    }
}
