package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
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
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1), null);
    PrivateKey ownerKey = PrivateKey.generate();
    PrivateKey supplyManagerKey = PrivateKey.generate();
    PrivateKey assetProtectionManagerKey = PrivateKey.generate();
    PrivateKey toKey = PrivateKey.generate();
    PrivateKey spenderKey = PrivateKey.generate();
    PrivateKey proposedOwnerKey = PrivateKey.generate();
    PrivateKey addrKey = PrivateKey.generate();
    Address owner = new Address(ownerKey.getPublicKey());
    Address supplyManager = new Address(supplyManagerKey.getPublicKey());
    Address assetProtectionManager = new Address(assetProtectionManagerKey.getPublicKey());
    Address to = new Address(toKey.getPublicKey());
    Address spender = new Address(spenderKey.getPublicKey());
    Address proposedOwner = new Address(proposedOwnerKey.getPublicKey());
    Address addr = new Address(addrKey.getPublicKey());
    BigInteger value = BigInteger.ONE;

    // prepare state
    String tokenName = "tokenName";
    String tokenSymbol = "tokenSymbol";
    BigInteger tokenDecimal = new BigInteger("2");
    BigInteger totalSupply = new BigInteger("10000");

    ConstructTransaction constructTransaction = new ConstructTransaction(
        ownerKey,
        tokenName,
        tokenSymbol,
        tokenDecimal,
        totalSupply,
        supplyManager,
        assetProtectionManager
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
            supplyManagerKey,
            to,
            value
        );

        var setKycTransaction = new SetKycPassedTransaction(ownerKey, to);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result =  Balances[addr]
        Assertions.assertEquals(value, state.getBalanceOf(to));

        // Transfer and check again
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(transferTransaction.toByteArray()));

        // Post-conditions: i. result =  Balances[addr]
        Assertions.assertEquals(value.add(value), state.getBalanceOf(to));
    }

    @Test
    public void getAllowanceTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        var setKycTransaction = new SetKycPassedTransaction(ownerKey, spender);
        var approveTransaction = new ApproveAllowanceTransaction(
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

        // Post-conditions: i. result = AssetProtectionManager
        Assertions.assertEquals(assetProtectionManager, state.getAssetProtectionManager());
    }

    @Test
    public void getProposedOwnerTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = ProposedOwner
        Assertions.assertEquals(Address.ZERO, state.getProposedOwner());

        // Propose owner then check again
        var setKycTransaction = new SetKycPassedTransaction(ownerKey, proposedOwner);
        var proposeOwnerTransaction = new ProposeOwnerTransaction(ownerKey, proposedOwner);

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
        var setKycTransaction = new SetKycPassedTransaction(ownerKey, proposedOwner);
        var freezeTransaction = new FreezeTransaction(ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-conditions: i. result = Frozen[addr]
        Assertions.assertTrue(state.isFrozen(addr));

        // Unfreeze addr then check again
        var unfreezeTransaction = new UnfreezeTransaction(ownerKey, addr);

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
        var setKycTransaction = new SetKycPassedTransaction(ownerKey, addr);

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
        var setKycTransaction = new SetKycPassedTransaction(ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // Post-conditions: i. result = (!Frozen[addr] && KycPassed[addr])
        Assertions.assertTrue(state.checkTransferAllowed(addr));

        // Freeze addr then check again
        var freezeTransaction = new FreezeTransaction(ownerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-conditions: i. result = (!Frozen[addr] && KycPassed[addr])
        Assertions.assertFalse(state.checkTransferAllowed(addr));
    }

    @Test
    public void isPrivilegedRoleTest() throws InvalidProtocolBufferException, SQLException {
        construct(topicListener, constructTransaction);

        // Precondition: i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(owner));

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(assetProtectionManager));

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertTrue(state.isPrivilegedRole(supplyManager));

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(spender));

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(to));

        // Post-conditions: i. result = (addr = Owner || addr = AssetProtectionManager ||addr = SupplyManager)
        Assertions.assertFalse(state.isPrivilegedRole(addr));
    }
}
