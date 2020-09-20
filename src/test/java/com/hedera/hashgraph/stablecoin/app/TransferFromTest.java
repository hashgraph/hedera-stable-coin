package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ApproveAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferFromTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class TransferFromTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void transferFromTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var fromKey = Ed25519PrivateKey.generate();
        var toKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var from = new Address(fromKey);
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
            caller,
            caller
        );

        var setKycTransactionTo = new SetKycPassedTransaction(0, callerKey, to);
        var setKycTransactionFrom = new SetKycPassedTransaction(0, callerKey, from);
        var transferTransaction = new TransferTransaction(0, callerKey, from, value);
        var approveAllowanceTransaction = new ApproveAllowanceTransaction(0, fromKey, caller, value);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransactionTo.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransactionFrom.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(approveAllowanceTransaction.toByteArray()));

        // prepare test transaction
        var transferFromTransaction = new TransferFromTransaction(
            0,
            callerKey,
            from,
            to,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iii.Balances[from] >= value
        Assertions.assertTrue(state.getBalanceOf(from).compareTo(value) >= 0);

        // iv.Allowances[from][caller] >= value
        Assertions.assertTrue(state.getAllowance(from, caller).compareTo(value) >= 0);

        // v.CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // vi.CheckTransferAllowed(from)
        Assertions.assertTrue(state.checkTransferAllowed(from));

        // vii.CheckTransferAllowed(to)
        Assertions.assertTrue(state.checkTransferAllowed(to));

        // Prepare for Post-check
        var fromBalance = state.getBalanceOf(from);
        var toBalance = state.getBalanceOf(to);
        var allowanceFromGrantedCaller = state.getAllowance(from, caller);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferFromTransaction.toByteArray()));

        // Post-Check

        // i. Balances[from]’ = Balances[from] - value
        Assertions.assertEquals(state.getBalanceOf(from), fromBalance.subtract(value));

        // ii.Allowances[from][caller]’ = Allowances[from][caller] - value
        Assertions.assertEquals(state.getAllowance(from, caller), allowanceFromGrantedCaller.subtract(value));

        // iii.Balances[to]’ = Balances[to] + value
        Assertions.assertEquals(state.getBalanceOf(to), toBalance.add(value));
    }
}
