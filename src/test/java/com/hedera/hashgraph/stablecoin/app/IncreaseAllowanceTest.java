package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.IncreaseAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class IncreaseAllowanceTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void increaseAllowanceTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var spenderKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var spender = new Address(spenderKey);
        var value = BigInteger.ONE;

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
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, spender);
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // get allowance before test
        var allowance = state.getAllowance(caller, spender);

        // prepare test transaction
        var increaseAllowanceTransaction = new IncreaseAllowanceTransaction(
            callerKey,
            spender,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iii. CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // iv. CheckTransferAllowed(spender)
        Assertions.assertTrue(state.checkTransferAllowed(spender));

        // v. Allowances[caller][spender] + value <= MAX_INT
        // Overflow not possible

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(increaseAllowanceTransaction.toByteArray()));

        // Post-Check

        // i. Allowances[caller][spender]’ = Allowances[caller][spender] + value
        Assertions.assertEquals(allowance.add(value), state.getAllowance(caller, spender));

        // Increase once more
        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iii. CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // iv. CheckTransferAllowed(spender)
        Assertions.assertTrue(state.checkTransferAllowed(spender));

        // v. Allowances[caller][spender] + value <= MAX_INT
        // Overflow not possible

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(increaseAllowanceTransaction.toByteArray()));

        // Post-Check

        // i. Allowances[caller][spender]’ = Allowances[caller][spender] + value
        // add value twice since we're increasing a second time
        Assertions.assertEquals(allowance.add(value).add(value), state.getAllowance(caller, spender));
    }
}
