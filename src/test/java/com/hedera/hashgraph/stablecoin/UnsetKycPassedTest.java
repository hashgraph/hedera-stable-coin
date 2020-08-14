package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.transaction.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.transaction.UnsetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class UnsetKycPassedTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void unsetKycPassedTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var assetManagerKey = PrivateKey.generate();
        var addrKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var assetManager = new Address(assetManagerKey.getPublicKey());
        var addr = new Address(addrKey.getPublicKey());

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
            assetManager
        );

        var setKycPassedTransaction = new SetKycPassedTransaction(
            callerKey,
            addr
        );

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        // prepare test transaction
        var unsetKycPassedTransaction = new UnsetKycPassedTransaction(
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertTrue(state.hasOwner());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. !isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // update State
        topicListener.handleTransaction(Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. !KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));

        // re-set and check for caller == AssetProtectionManager instead this time
        topicListener.handleTransaction(Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        Assertions.assertTrue(state.isKycPassed(addr));

        var unsetKycPassedTransaction2 = new SetKycPassedTransaction(
            assetManagerKey,
            addr
        );

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(unsetKycPassedTransaction2.toByteArray()));

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertTrue(state.hasOwner());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(assetManager, state.getAssetProtectionManager());

        // iii. !isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // update State
        topicListener.handleTransaction(Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. !KycPassed[addr]
        Assertions.assertFalse(state.isKycPassed(addr));
    }
}
