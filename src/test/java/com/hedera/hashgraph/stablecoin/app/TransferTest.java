package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class TransferTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void transferTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var toKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var to = new Address(toKey);
        var value = BigInteger.ONE;

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
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(0, callerKey, to);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var transferTransaction = new TransferTransaction(
            0,
            callerKey,
            to,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iii. Balances[caller] >= value
        Assertions.assertTrue(state.getBalanceOf(caller).compareTo(value) >= 0);

        // iv. CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // v. CheckTransferAllowed(to)
        Assertions.assertTrue(state.checkTransferAllowed(to));

        // Prepare for Post-check
        var callerBalance = state.getBalanceOf(caller);
        var toBalance = state.getBalanceOf(to);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));

        // Post-Check

        // i.Balances[caller]’ = Balances[caller] - value
        Assertions.assertEquals(state.getBalanceOf(caller), callerBalance.subtract(value));

        // ii.Balances[to]' = Balances[to] + value
        Assertions.assertEquals(state.getBalanceOf(to), toBalance.add(value));
    }
}
