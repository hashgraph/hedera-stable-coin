package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.DecreaseAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.IncreaseAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class DecreaseAllowanceTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), null);

    @Test
    public void decreaseAllowanceTest() throws InvalidProtocolBufferException, SQLException {
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
            0,
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            caller,
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(0, callerKey, spender);
        var increaseAllowanceTransaction = new IncreaseAllowanceTransaction(0, callerKey, spender, BigInteger.TWO);
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(increaseAllowanceTransaction.toByteArray()));

        // get allowance before test
        var allowance = state.getAllowance(caller, spender);

        // prepare test transaction
        var decreaseAllowanceTransaction = new DecreaseAllowanceTransaction(0, callerKey, spender, value);

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iii. CheckTransferAllowed(caller)
        Assertions.assertTrue(state.checkTransferAllowed(caller));

        // iv. CheckTransferAllowed(spender)
        Assertions.assertTrue(state.checkTransferAllowed(spender));

        // v. Allowances[caller][spender] >= value
        Assertions.assertTrue(state.getAllowance(caller, spender).compareTo(value) >= 0);

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(decreaseAllowanceTransaction.toByteArray()));

        // Post-Check

        // i. Allowances[caller][spender]’ = Allowances[caller][spender] - value
        Assertions.assertEquals(allowance.subtract(value), state.getAllowance(caller, spender));
    }
}
