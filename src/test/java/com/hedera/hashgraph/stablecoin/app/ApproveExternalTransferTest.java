package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class ApproveExternalTransferTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void approveExternalTransferTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey.publicKey);
        var to = "StableCoin".getBytes();
        var network = "www.stablecoin.com";
        var amount = BigInteger.ONE;

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
            caller,
            caller
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));

        // prepare test transaction
        var approveExternalTransferTransaction = new ApproveExternalTransferTransaction(
            callerKey,
            network,
            to,
            amount
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. amount >= 0
        Assertions.assertTrue(amount.compareTo(BigInteger.ZERO) >= 0);

        // iii. CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // iv. amount <= MAX_INT
        Assertions.assertTrue(amount.compareTo(BigInteger.TWO.pow(256)) < 0);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(approveExternalTransferTransaction.toByteArray()));

        // Post-Check

        // i. Allowances[caller][spender] = value
        Assertions.assertEquals(amount, state.getExternalAllowance(caller, network, to));
    }
}
