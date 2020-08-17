package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.transaction.FreezeTransaction;
import com.hedera.hashgraph.stablecoin.transaction.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.transaction.TransferTransaction;
import com.hedera.hashgraph.stablecoin.transaction.UnfreezeTransaction;
import com.hedera.hashgraph.stablecoin.transaction.WipeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class WipeTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void wipeTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var assetProtectionManagerKey = PrivateKey.generate();
        var addrKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var assetProtectionManager = new Address(assetProtectionManagerKey.getPublicKey());
        var addr = new Address(addrKey.getPublicKey());
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
            assetProtectionManager
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, addr);
        var transferTransaction = new TransferTransaction(callerKey, addr, value);
        var freezeTransaction = new FreezeTransaction(callerKey, addr);

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(transferTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(freezeTransaction.toByteArray()));

        // prepare test transaction
        var wipeTransaction = new WipeTransaction(
            callerKey,
            addr
        );

        var balance = state.getBalanceOf(addr);

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. Frozen[addr]
        Assertions.assertTrue(state.isFrozen(addr));

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(wipeTransaction.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply - Balances[addr] // total supply decreased
        Assertions.assertEquals(totalSupply.subtract(balance), state.getTotalSupply());

        // ii. Balances[addr]’ = 0 // balance “updated” to 0
        Assertions.assertEquals(BigInteger.ZERO, state.getBalanceOf(addr));

        // re-establish address, transfer to it, then freeze it and check for caller == AssetProtectionManager instead this time
        totalSupply = state.getTotalSupply();
        var unfreezeTransaction = new UnfreezeTransaction(callerKey, addr);
        topicListener.handleTransaction(Transaction.parseFrom(setKycTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(unfreezeTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(transferTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(freezeTransaction.toByteArray()));

        // prepare test transaction
        var wipeTransaction2 = new WipeTransaction(
            assetProtectionManagerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(assetProtectionManager, state.getAssetProtectionManager());

        // iii. Frozen[addr]
        Assertions.assertTrue(state.isFrozen(addr));

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(wipeTransaction2.toByteArray()));

        // Post-Check

        // i. TotalSupply’ = TotalSupply - Balances[addr] // total supply decreased
        Assertions.assertEquals(totalSupply.subtract(balance), state.getTotalSupply());

        // ii. Balances[addr]’ = 0 // balance “updated” to 0
        Assertions.assertEquals(BigInteger.ZERO, state.getBalanceOf(addr));
    }
}
