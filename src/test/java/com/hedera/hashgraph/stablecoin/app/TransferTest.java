package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class TransferTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void transferTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var toKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var to = new Address(toKey.getPublicKey());
        var value = BigInteger.ONE;

        // prepare state
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = new BigInteger("2");
        var totalSupply = new BigInteger("10000");

        var constructTransaction = new ConstructTransaction(
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            caller,
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, to);

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var transferTransaction = new TransferTransaction(
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
        topicListener.handleTransaction(Transaction.parseFrom(transferTransaction.toByteArray()));

        // Post-Check

        // i.Balances[caller]’ = Balances[caller] - value
        Assertions.assertEquals(state.getBalanceOf(caller), callerBalance.subtract(value));

        // ii.Balances[to]' = Balances[to] + value
        Assertions.assertEquals(state.getBalanceOf(to), toBalance.add(value));
    }
}
