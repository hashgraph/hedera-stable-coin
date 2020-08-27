package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
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
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1), null);

    @Test
    public void claimOwnershipTest() throws InvalidProtocolBufferException, SQLException {
        var ownerKey = PrivateKey.generate();
        var callerKey = PrivateKey.generate();
        var owner = new Address(callerKey.getPublicKey());
        var caller = new Address(callerKey.getPublicKey());

        // prepare state
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = new BigInteger("2");
        var totalSupply = new BigInteger("10000");

        var constructTransaction = new ConstructTransaction(
            ownerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            owner,
            owner
        );

        var setKycTransaction = new SetKycPassedTransaction(ownerKey, caller);
        var proposeOwnerTransaction = new ProposeOwnerTransaction(ownerKey, caller);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(proposeOwnerTransaction.toByteArray()));

        // prepare test transaction
        var claimOwnershipTransaction = new ClaimOwnershipTransaction(
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
