package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.FreezeTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.UnfreezeTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class FreezeTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1), null);

    @Test
    public void freezeTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = PrivateKey.generate();
        var assetProtectionManagerKey = PrivateKey.generate();
        var addrKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var assetProtectionManager = new Address(assetProtectionManagerKey.getPublicKey());
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
            assetProtectionManager
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var freezeTransaction = new FreezeTransaction(
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii.!isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction.toByteArray()));

        // Post-Check

        // i. AssetProtectionManager = addr
        Assertions.assertTrue(state.isFrozen(addr));

        // unfreeze and check for caller == AssetProtectionManager instead this time
        var unfreezeTransaction = new UnfreezeTransaction(callerKey, addr);
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(unfreezeTransaction.toByteArray()));

        // prepare test transaction
        var freezeTransaction2 = new FreezeTransaction(
            assetProtectionManagerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii.caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(assetProtectionManager, state.getAssetProtectionManager());

        // iii.!isPrivilegedRole(addr)
        Assertions.assertFalse(state.isPrivilegedRole(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(freezeTransaction2.toByteArray()));

        // Post-Check

        // i. AssetProtectionManager = addr
        Assertions.assertTrue(state.isFrozen(addr));
    }
}
