package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ClaimOwnershipTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ProposeOwnerTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class ClaimOwnershipTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void claimOwnershipTest() throws InvalidProtocolBufferException, SQLException {
        var ownerKey = Ed25519PrivateKey.generate();
        var callerKey = Ed25519PrivateKey.generate();
        var owner = new Address(callerKey);
        var caller = new Address(callerKey);

        // prepare state
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = 2;
        var totalSupply = new BigInteger("10000");

        var constructTransaction = new ConstructTransaction(
            0,
            ownerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            owner,
            owner,
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, caller);
        var proposeOwnerTransaction = new ProposeOwnerTransaction(0, ownerKey, caller);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(proposeOwnerTransaction.toByteArray()));

        // prepare test transaction
        var claimOwnershipTransaction = new ClaimOwnershipTransaction(
            0,
            callerKey
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = ProposedOwner
        Assertions.assertEquals(caller, state.getProposedOwner());

        // iii.CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(claimOwnershipTransaction.toByteArray()));

        // Post-Check

        // i.Owner = caller
        Assertions.assertEquals(caller, state.getOwner());

        // ii.ProposedOwner = 0x
        Assertions.assertTrue(state.getProposedOwner().isZero());
    }
}
