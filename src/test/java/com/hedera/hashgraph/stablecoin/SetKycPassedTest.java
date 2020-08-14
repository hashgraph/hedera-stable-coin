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

public class SetKycPassedTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void setKycPassedTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var caller2Key = PrivateKey.generate();
        var addrKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var caller2 = new Address(caller2Key.getPublicKey());
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
            caller2
        );

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));

        // prepare test transaction
        var setKycPassedTransaction = new SetKycPassedTransaction(
            callerKey,
            addr
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertTrue(state.hasOwner());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(setKycPassedTransaction.toByteArray()));

        // Post-Check

        // i. KycPassed[addr]
        Assertions.assertTrue(state.isKycPassed(addr));

        // unset and check for caller == AssetProtectionManager instead this time
        var unsetKycPassedTransaction = new UnsetKycPassedTransaction(
            callerKey,
            addr
        );

        // update State
        topicListener.handleTransaction(Transaction.parseFrom(unsetKycPassedTransaction.toByteArray()));

        Assertions.assertFalse(state.isKycPassed(addr));

        var setKycPassedTransaction2 = new SetKycPassedTransaction(
            caller2Key,
            addr
        );

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(setKycPassedTransaction2.toByteArray()));

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertTrue(state.hasOwner());

        // ii. caller = AssetProtectionManager || caller = Owner
        Assertions.assertEquals(state.getAssetProtectionManager(), caller2);

        // Post-Check

        // i. KycPassed[addr]
        Assertions.assertTrue(state.isKycPassed(addr));
    }
}
