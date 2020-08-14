package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.transaction.ApproveAllowanceTransaction;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.transaction.MintTransaction;
import com.hedera.hashgraph.stablecoin.transaction.SetKycPassedTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class MintTest {
    State state = new State();
    Client client = Client.forTestnet();
    TopicListener topicListener = new TopicListener(state, client, new TopicId(1));

    @Test
    public void mintTest() throws InvalidProtocolBufferException {
        var callerKey = PrivateKey.generate();
        var supplyManagerKey = PrivateKey.generate();
        var caller = new Address(callerKey.getPublicKey());
        var supplyManager = new Address(supplyManagerKey.getPublicKey());
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
            supplyManager,
            caller
        );

        topicListener.handleTransaction(Transaction.parseFrom(constructTransaction.toByteArray()));

        // prepare test transaction
        var mintTransaction = new MintTransaction(
            callerKey,
            value
        );

        // Pre-Check

        // i. Owner != 0x
        Assertions.assertTrue(state.hasOwner());

        // ii.caller = SupplyManager || caller = Owner
        Assertions.assertEquals(caller, state.getOwner());

        //iii.value >= 0
        Assertions.assertTrue(value.compareTo(BigInteger.ZERO) >= 0);

        // iv.TotalSupply + value <= MAX_INT // prevents overflow
        Assertions.assertTrue(state.getTotalSupply().add(value).compareTo(State.MAX_INT) <= 0);

        // v.TotalSupply >= Balances[SupplyManager]
        Assertions.assertTrue(state.getTotalSupply().compareTo(state.getBalanceOf(supplyManager)) >= 0);

        // Prepare Post-check
        var supplyManagerBalance = state.getBalanceOf(supplyManager);

        // Update State
        topicListener.handleTransaction(Transaction.parseFrom(mintTransaction.toByteArray()));

        // Post-Check

        // i.TotalSupply’ = TotalSupply + value // the new supply is increased by value
        Assertions.assertEquals(totalSupply.add(value), state.getTotalSupply());

        // ii.Balances[SupplyManager]’ = Balances[SupplyManager] + value
        Assertions.assertEquals(supplyManagerBalance.add(value), state.getBalanceOf(supplyManager));
    }
}
