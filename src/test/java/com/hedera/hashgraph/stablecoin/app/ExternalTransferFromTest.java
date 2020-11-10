package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ExternalTransferFromTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.Instant;

public class ExternalTransferFromTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void transferFromTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var from = "StableCoin".getBytes(Charset.forName("UTF-8"));
        var network = "www.stablecoin.com";
        var toKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var to = new Address(toKey);
        var amount = BigInteger.ONE;

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

        var setKycTransactionTo = new SetKycPassedTransaction(0, callerKey, to);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransactionTo.toByteArray()));

        // prepare test transaction
        var externalTransferFromTransaction = new ExternalTransferFromTransaction(
            0,
            callerKey,
            from,
            network,
            to,
            amount
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = SupplyManager
        Assertions.assertEquals(caller, state.getSupplyManager());

        // iii. amount >= 0
        Assertions.assertTrue(amount.compareTo(BigInteger.ZERO) >= 0);

        // iv. CheckTransferAllowed(to)
        Assertions.assertTrue(state.checkTransferAllowed(to));

        // v. amount <= MAX_INT
        Assertions.assertTrue(amount.compareTo(BigInteger.TWO.pow(256)) < 0);

        // Prepare for Post-check
        var toBalance = state.getBalanceOf(to);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(externalTransferFromTransaction.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply + value
        Assertions.assertEquals(state.getTotalSupply(), totalSupply.add(amount));

        // ii. Balances[to]’ = Balances[to] + value
        Assertions.assertEquals(state.getBalanceOf(to), toBalance.add(amount));
    }
}
