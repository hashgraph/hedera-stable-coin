package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ChangeAssetProtectionManagerTransaction;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.time.Instant;

public class ChangeAssetProtectionManagerTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1), null);

    @Test
    public void changeAssetProtectionManagerTest() throws InvalidProtocolBufferException, SQLException {
        var callerKey = PrivateKey.generate();
        var addrKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
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
            caller
        );

        var setKycTransaction = new SetKycPassedTransaction(callerKey, addr);

        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(constructTransaction.toByteArray()));
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(setKycTransaction.toByteArray()));

        // prepare test transaction
        var changeAssetProtectionManagerTransaction = new ChangeAssetProtectionManagerTransaction(
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertFalse(state.getOwner().isZero());

        // ii. caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // iii. addr != 0x
        Assertions.assertFalse(addr.isZero());

        // iv. CheckTransferAllowed(addr)
        Assertions.assertTrue(state.checkTransferAllowed(addr));

        // Update State
        topicListener.handleTransaction(Instant.EPOCH, Transaction.parseFrom(changeAssetProtectionManagerTransaction.toByteArray()));

        // Post-Check

        // i. AssetProtectionManager = addr
        Assertions.assertEquals(addr, state.getAssetProtectionManager());
    }
}
