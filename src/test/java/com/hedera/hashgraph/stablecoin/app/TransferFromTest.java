package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
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

public class TransferFromTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void transferFromTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var fromKey = PrivateKey.generate();
        var toKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var from = new Address(fromKey.getPublicKey());
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

        var setKycTransactionTo = new SetKycPassedTransaction(callerKey, to);
        var setKycTransactionFrom = new SetKycPassedTransaction(callerKey, from);
        var transferTransaction = new TransferTransaction(callerKey, from, value);
        var approveAllowanceTransaction = new ApproveAllowanceTransaction(fromKey, caller, value);

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(setKycTransactionTo.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(setKycTransactionFrom.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(transferTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(approveAllowanceTransaction.toByteArray()));

        // prepare test transaction
        var transferFromTransaction = new TransferFromTransaction(
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
        topicListener.handleTransaction(Transaction.parseFrom(transferFromTransaction.toByteArray()));

        // Post-Check

        // i. Balances[from]’ = Balances[from] - value
        Assertions.assertEquals(state.getBalanceOf(from), fromBalance.subtract(value));

        // ii.Allowances[from][caller]’ = Allowances[from][caller] - value
        Assertions.assertEquals(state.getAllowance(from, caller), allowanceFromGrantedCaller.subtract(value));

        // iii.Balances[to]’ = Balances[to] + value
        Assertions.assertEquals(state.getBalanceOf(to), toBalance.add(value));
    }
}
