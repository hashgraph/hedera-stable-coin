package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
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

public class SetKycPassedTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void setKycPassedTest() throws InvalidProtocolBufferException, SQLException {
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
            0,
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            caller,
            assetManager,
            caller
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));

        // prepare test transaction
        var setKycPassedTransaction = new SetKycPassedTransaction(
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
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. KycPassed[addr]
        Assertions.assertTrue(state.isKycPassed(addr));

        // unset and check for caller == complianceManager instead this time
        var unsetKycPassedTransaction = new UnsetKycPassedTransaction(
            0,
            callerKey,
            addr
        );

        // update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        Assertions.assertFalse(state.isKycPassed(addr));

        var setKycPassedTransaction2 = new SetKycPassedTransaction(
            0,
            assetManagerKey,
            addr
        );

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycPassedTransaction2.toByteArray()));

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = complianceManager || caller = Owner
        Assertions.assertEquals(assetManager, state.getComplianceManager());

        // Post-Check

        // i. KycPassed[addr]
        Assertions.assertTrue(state.isKycPassed(addr));
    }
}
