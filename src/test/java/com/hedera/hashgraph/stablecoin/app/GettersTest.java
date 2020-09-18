package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ApproveAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.FreezeTransaction;
import com.hedera.hashgraph.stablecoin.sdk.IncreaseAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ProposeOwnerTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import com.hedera.hashgraph.stablecoin.sdk.UnfreezeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class GettersTest {
    State state = new State();
    TopicListener topicListener = new TopicListener(state, null, new ConsensusTopicId(0,0, 1), null);
    Ed25519PrivateKey ownerKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey supplyManagerKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey complianceManagerKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey enforcementManagerKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey toKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey spenderKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey proposedOwnerKey = Ed25519PrivateKey.generate();
    Ed25519PrivateKey addrKey = Ed25519PrivateKey.generate();
    Address owner = new Address(ownerKey.publicKey);
    Address supplyManager = new Address(supplyManagerKey.publicKey);
    Address complianceManager = new Address(complianceManagerKey.publicKey);
    Address enforcementManager = new Address(enforcementManagerKey.publicKey);
    Address to = new Address(toKey.publicKey);
    Address spender = new Address(spenderKey.publicKey);
    Address proposedOwner = new Address(proposedOwnerKey.publicKey);
    Address addr = new Address(addrKey.publicKey);
    BigInteger value = BigInteger.ONE;

    // prepare state
    String tokenName = "tokenName";
    String tokenSymbol = "tokenSymbol";
    int tokenDecimal = 2;
    BigInteger totalSupply = new BigInteger("10000");

    ConstructTransaction constructTransaction = new ConstructTransaction(
        0,
        ownerKey,
        tokenName,
        tokenSymbol,
        tokenDecimal,
        totalSupply,
        supplyManager,
        complianceManager,
        enforcementManager
    );

    static void construct(TopicListener topicListener, ConstructTransaction constructTransaction) throws InvalidProtocolBufferException, SQLException {
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
    }

    @Test
    public void getTokenNameTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = TokenName
        Assertions.assertEquals(tokenName, state.getTokenName());
    }

    @Test
    public void getTokenSymbolTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = TokenSymbol
        Assertions.assertEquals(tokenSymbol, state.getTokenSymbol());
    }

    @Test
    public void getTokenDecimalTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = TokenDecimal
        Assertions.assertEquals(tokenDecimal, state.getTokenDecimal());
    }

    @Test
    public void getTotalSupplyTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = TotalSupply
        Assertions.assertEquals(totalSupply, state.getTotalSupply());
    }

    @Test
    public void getBalanceTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        var transferTransaction = new TransferTransaction(
            0,
            supplyManagerKey,
            to,
            value
        );

        var secondTransferTransaction = new TransferTransaction(
            0,
            supplyManagerKey,
            to,
            value
        );

        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, to);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result =  Balances[addr]
        Assertions.assertEquals(value, state.getBalanceOf(to));

        // Transfer and check again
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(secondTransferTransaction.toByteArray()));

        // Post-conditions: i. result =  Balances[addr]
        Assertions.assertEquals(value.add(value), state.getBalanceOf(to));
    }

    @Test
    public void getAllowanceTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, spender);
        var approveTransaction = new ApproveAllowanceTransaction(
            0,
            supplyManagerKey,
            spender,
            value
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(approveTransaction.toByteArray()));

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = Allowances[addr][spender]
        Assertions.assertEquals(value, state.getAllowance(supplyManager, spender));

        // increase allowance and check again
        var increaseAllowanceTransaction = new IncreaseAllowanceTransaction(
            0,
            supplyManagerKey,
            spender,
            value
        );

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(increaseAllowanceTransaction.toByteArray()));

        // Post-conditions: i. result = Allowances[addr][spender]
        Assertions.assertEquals(value.add(value), state.getAllowance(supplyManager, spender));
    }

    @Test
    public void getOwnerTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = Owner
        Assertions.assertEquals(owner, state.getOwner());
    }

    @Test
    public void getSupplyManager() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = SupplyManager
        Assertions.assertEquals(supplyManager, state.getSupplyManager());
    }

    @Test
    public void getAssetManagerTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = complianceManager
        Assertions.assertEquals(complianceManager, state.getComplianceManager());
    }

    @Test
    public void getProposedOwnerTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = ProposedOwner
        Assertions.assertEquals(Address.ZERO, state.getProposedOwner());

        // Propose owner then check again
        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, proposedOwner);
        var proposeOwnerTransaction = new ProposeOwnerTransaction(0, ownerKey, proposedOwner);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(proposeOwnerTransaction.toByteArray()));

        // Post-conditions: i. result = ProposedOwner
        Assertions.assertEquals(proposedOwner, state.getProposedOwner());
    }

    @Test
    public void isFrozenTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = Frozen[addr]
        Assertions.assertFalse(state.isFrozen(addr));

        // Freeze addr then check again
        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, proposedOwner);
        var freezeTransaction = new FreezeTransaction(0, ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-conditions: i. result = Frozen[addr]
        Assertions.assertTrue(state.isFrozen(addr));

        // Unfreeze addr then check again
        var unfreezeTransaction = new UnfreezeTransaction(0, ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransaction.toByteArray()));

        // Post-conditions: i. result = Frozen[addr]
        Assertions.assertFalse(state.isFrozen(addr));
    }

    @Test
    public void isKycPassedTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));

        // Set Kyc passed for addr then check again
        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // Post-conditions: i. result = KycPassed[addr]
        Assertions.assertTrue(state.isKycPassed(addr));
    }

    @Test
    public void checkTransferAllowedTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = (!Frozen[addr] && KycPassed[addr])
        Assertions.assertFalse(state.checkTransferAllowed(addr));

        // Set Kyc passed for addr then check again
        var setKycTransaction = new SetKycPassedTransaction(0, ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // Post-conditions: i. result = (!Frozen[addr] && KycPassed[addr])
        Assertions.assertTrue(state.checkTransferAllowed(addr));

        // Freeze addr then check again
        var freezeTransaction = new FreezeTransaction(0, ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-conditions: i. result = (!Frozen[addr] && KycPassed[addr])
        Assertions.assertFalse(state.checkTransferAllowed(addr));
    }

    @Test
    public void isPrivilegedRoleTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(owner));

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(complianceManager));

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(supplyManager));

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(spender));

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(to));

        // Post-conditions: i. result = (addr = Owner || addr = complianceManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(addr));
    }
}
