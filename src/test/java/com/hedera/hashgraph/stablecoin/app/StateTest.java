package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.app.repository.GetTransactionStatus;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class StateTest {
    @Test
    public void stateTest() throws InvalidProtocolBufferException, SQLException {
        State state = new State();

        //TokenName : String = “”
        Assertions.assertEquals("", state.getTokenName());

        // TokenSymbol : String = “”
        Assertions.assertEquals("",state.getTokenSymbol());

        // TokenDecimal : Int = 0
        Assertions.assertEquals(0, state.getTokenDecimal());

        // TotalSupply : Int = 0
        Assertions.assertEquals(new BigInteger("0"), state.getTotalSupply());

        // Owner : Address = 0x
        Assertions.assertSame(Address.ZERO, state.getOwner());

        // SupplyManager: Address = {}
        Assertions.assertSame(Address.ZERO, state.getSupplyManager());

        // complianceManager: Address = {}
        Assertions.assertSame(Address.ZERO, state.getComplianceManager());

        // Balances: Map::Address->Int = {}
        Assertions.assertTrue(state.balances.isEmpty());

        // Allowances: Map::Address->(Map::Address->Int) = {}
        Assertions.assertTrue(state.allowances.isEmpty());

        // Frozen: Map::Address->Bool = {}
        Assertions.assertTrue(state.frozen.isEmpty());

        // KycPassed: Map::Address->Bool = {}
        Assertions.assertTrue(state.kycPassed.isEmpty());

        // ProposedOwner: Address = 0x
        Assertions.assertSame(Address.ZERO, state.getProposedOwner());

        // prepare test transaction for tests
        GetTransactionStatus getTransactionStatus = new GetTransactionStatus(new SqlConnectionManager());
        TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0), getTransactionStatus);

        var callerKey = Ed25519PrivateKey.generate();
        var caller = new Address(callerKey);
//        @Var var supplyManager = caller;
//        @Var var complianceManager = caller;
//        @Var var enforcementManager = caller;
        var tokenName = "tokenName";
        var tokenSymbol = "tokenSymbol";
        var tokenDecimal = 2;
        @Var var totalSupply = new BigInteger("10000");

        // Prepare Transaction
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

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));


        // Test removeExpiredTransactionReceipts()
        // Test that non-expired receipts are not removed
        // This should do nothing as transaction receipt is not old enough to be expired
        state.removeExpiredTransactionReceipts();

        // Assert map is not empty
        Assertions.assertFalse(state.transactionReceipts.isEmpty());

        // Test that expired receipts are removed
        // Prepare fake transactionId
        var transactionId = TransactionId.withValidStart(new AccountId(555), Instant.now().minus(3, ChronoUnit.MINUTES));

        // Prepare fake transactionReceipt
        var transactionReceipt = new TransactionReceipt(Instant.now(), caller, transactionId, state.transactionReceipts.entrySet().iterator().next().getValue().status);

        // Clear transactionReceipts map and add fake transactionId as key for the real receipt
        state.transactionReceipts.clear();
        state.addTransactionReceipt(transactionId, transactionReceipt);

        // Assert map is not empty
        Assertions.assertNotNull(state.getTransactionReceipt(transactionId));

        // Remove fake entry as transactionId is now > 3 mins old
        state.removeExpiredTransactionReceipts();

        // Assert map is empty
        Assertions.assertTrue(state.transactionReceipts.isEmpty());


        // Test checkTransferAllowed(), other tests cover all cases but this one
        var addr = new Address(Ed25519PrivateKey.generate().publicKey);
        state.setOwner(Address.ZERO);
        Assertions.assertFalse(state.checkTransferAllowed(addr));

        // Test isPrivilegedRole()
        state.setEnforcementManager(addr);

        Assertions.assertTrue(state.isPrivilegedRole(addr));

        // Test getTimestamp()
        Assertions.assertEquals(Instant.EPOCH, state.getTimestamp());

        // Test setTotalSupply()
        totalSupply = new BigInteger("100");
        state.setTotalSupply(totalSupply);
        Assertions.assertEquals(totalSupply, state.getTotalSupply());
    }
}
