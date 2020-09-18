package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ApproveAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class TransactionHandlerTest {
    State state = new State();
    GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

    @Test
    public void transactionHandlerTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = Ed25519PrivateKey.generate();
        var complianceManagerKey = Ed25519PrivateKey.generate();
        var addrKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
        var complianceManager = new Address(complianceManagerKey);
        var addr = new Address(addrKey);

        // prepare state
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = 2;
        var totalSupply = new BigInteger("10000");

        // Test for TokenDecimal < 0
        var constructTransactionFail = new ConstructTransaction(
            callerKey,
            tokenName,
            tokenSymbol,
            -1,
            totalSupply,
            caller,
            complianceManager,
            caller
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransactionFail.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO, getTransactionStatus.status);


        // Test for Owner = 0x
        var constructTransaction = new ConstructTransaction(
            callerKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            caller,
            complianceManager,
            caller
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));

        // This should never happen
        state.setOwner(Address.ZERO);

        // Make any transaction in order to test ensureOwnerSet()
        var setKycTransactionTo = new SetKycPassedTransaction(callerKey, addr);

        // Should fail with Status.OWNER_NOT_SET
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransactionTo.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.OWNER_NOT_SET, getTransactionStatus.status);


        // Re-Set owner
        state.setOwner(caller);

        // Test for value >= MAX_INT
        var approveAllowance = new ApproveAllowanceTransaction(callerKey, caller, BigInteger.TWO.pow(256).add(BigInteger.ONE));

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(approveAllowance.toByteArray()));

        // Check that status is the correct failure type
        Assertions.assertEquals(Status.NUMBER_VALUES_LIMITED_TO_256_BITS, getTransactionStatus.status);
    }
}
