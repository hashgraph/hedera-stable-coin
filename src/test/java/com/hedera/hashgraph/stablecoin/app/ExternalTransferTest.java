package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ApproveExternalTransferTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ExternalTransferTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.Instant;

public class ExternalTransferTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void transferFromTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var fromKey = Ed25519PrivateKey.generate();
        var network = "www.stablecoin.com";
        var to = "StableCoin".getBytes(Charset.forName("UTF-8"));
        var caller = new Address(callerKey);
        var from = new Address(fromKey);
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

        var setKycTransactionTo = new SetKycPassedTransaction(0, callerKey, from);
        var transfer = new TransferTransaction(0, callerKey, from, amount);
        var approveExternalTransfer = new ApproveExternalTransferTransaction(0, fromKey, network, to, amount);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransactionTo.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transfer.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(approveExternalTransfer.toByteArray()));


        // prepare test transaction
        var externalTransferFromTransaction = new ExternalTransferTransaction(
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

        // iv. CheckTransferAllowed(from)
        Assertions.assertTrue(state.checkTransferAllowed(from));

        // v. amount <= MAX_INT
        Assertions.assertTrue(amount.compareTo(BigInteger.TWO.pow(256)) < 0);

        // vi. externalAllowanceOf(args.from, args.networkURI, args.to) >= value
        Assertions.assertTrue(state.getExternalAllowance(from, network, to).compareTo(amount) >= 0);

        // Prepare for Post-check
        var fromBalance = state.getBalanceOf(from);
        var externalAllowance = state.getExternalAllowance(from, network, to);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(externalTransferFromTransaction.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply - value
        Assertions.assertEquals(state.getTotalSupply(), totalSupply.subtract(amount));

        // ii. Balances[from]’ = Balances[from] - value
        Assertions.assertEquals(state.getBalanceOf(from), fromBalance.subtract(amount));

        // iii. Allowances[(from, networkURI, to)] -= value
        Assertions.assertEquals(state.getExternalAllowance(from, network, to), externalAllowance.subtract(amount));
    }
}
