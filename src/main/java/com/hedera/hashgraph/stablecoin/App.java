package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.*;
import com.hedera.hashgraph.stablecoin.proto.ConstructTransactionData;
import com.hedera.hashgraph.stablecoin.proto.Transaction;
import com.hedera.hashgraph.stablecoin.proto.TransactionBody;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.transaction.MintTransaction;
import com.hedera.hashgraph.stablecoin.transaction.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.transaction.TransferTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class App {
    public static void main(String[] args) throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException, InterruptedException {
        var env = Dotenv.configure().ignoreIfMissing().load();

        // configure a client to connect to Hedera
        // todo: we need an .env variable to allow switching network
        var client = Client.forTestnet();

        var operatorId = AccountId.fromString(Objects.requireNonNull(
            env.get("HSC_OPERATOR_ID"), "missing environment variable HSC_OPERATOR_ID"));

        var operatorKey = PrivateKey.fromString(Objects.requireNonNull(
            env.get("HSC_OPERATOR_KEY"), "missing environment variable HSC_OPERATOR_ID"));

        // configure the client operator
        client.setOperator(operatorId, operatorKey);

        var maybeTopicId = Optional.ofNullable(env.get("HSC_TOPIC_ID")).map(TopicId::fromString);

        var operatorAddress = new Address(operatorKey.getPublicKey());

        if (maybeTopicId.isEmpty()) {
            // if we were not given a topic ID
            // we need to create a new topic, and send a construct message,
            // which establishes our operator as the owner

            // we need the token properties to do this
            var tokenName = Objects.requireNonNull(
                env.get("HSC_TOKEN_NAME"), "missing environment variable HSC_TOKEN_NAME");

            var tokenSymbol = Objects.requireNonNull(
                env.get("HSC_TOKEN_SYMBOL"), "missing environment variable HSC_TOKEN_SYMBOL");

            var tokenDecimal = new BigInteger(Objects.requireNonNull(
                env.get("HSC_TOKEN_DECIMAL"), "missing environment variable HSC_TOKEN_DECIMAL"));

            var totalSupply = new BigInteger(Objects.requireNonNull(
                env.get("HSC_TOTAL_SUPPLY"), "missing environment variable HSC_TOTAL_SUPPLY"));

            // create the new topic ID
            maybeTopicId = Optional.ofNullable(new TopicCreateTransaction()
                .setAdminKey(operatorKey)
                .execute(client)
                .transactionId
                .getReceipt(client)
                .topicId);

            // after a topic create transaction, this will be set
            // or we would have died elsewhere
            assert maybeTopicId.isPresent();

            System.out.println("Created topic " + maybeTopicId.get());

            // now we need to create a <Construct> transaction

            var constructTransactionBytes = new ConstructTransaction(
                operatorKey,
                tokenName,
                tokenSymbol,
                tokenDecimal,
                totalSupply,
                // fixme: allow these to be configured
                operatorAddress,
                operatorAddress
            ).toByteArray();

            // and finally submit it

            new TopicMessageSubmitTransaction()
                .setTopicId(maybeTopicId.get())
                .setMessage(constructTransactionBytes)
                .execute(client)
                .transactionId
                .getReceipt(client);

            System.out.println("Sent <Construct> transaction to topic " + maybeTopicId.get());
        }

        final var topicId = maybeTopicId.get();

        // wait 10s because subscribe retries on the SDK v2 are broken
        // fixme: look into those
        Thread.sleep(10_000);

        // create a new instance of in-memory state
        // todo: load from a snapshot?
        var state = new State();

        // create a new topic listener, and start listening
        new TopicListener(state, client, topicId).startListening();

        // wait while the APIs and the topic listener run in the background
        // todo: listen to SIGINT/SIGTERM
//        while (true) Thread.sleep(0);

        // mint a lot
        new TopicMessageSubmitTransaction()
            .setTopicId(topicId)
            .setMessage(new MintTransaction(operatorKey, new BigInteger("1000000000000000000000")).toByteArray())
            .execute(client);

        System.out.println("Sent <Mint> transaction to topic " + maybeTopicId.get());

        // create second account to transfer to
        var accountToTransferTo = new Address(PrivateKey.generate().getPublicKey());

        new TopicMessageSubmitTransaction()
            .setTopicId(topicId)
            .setMessage(new SetKycPassedTransaction(operatorKey, accountToTransferTo).toByteArray())
            .execute(client);

        System.out.println("Sent <SetKycPassed> transaction to topic " + maybeTopicId.get());

        // sleep to wait for listener to catch up
        Thread.sleep(500);

        // generate many, many transfers
        while (true) {
            new TopicMessageSubmitTransaction()
                .setTopicId(topicId)
                .setMessage(new TransferTransaction(operatorKey, accountToTransferTo, BigInteger.ONE).toByteArray())
                .execute(client);

            System.out.println("Sent <Transfer> transaction to topic " + maybeTopicId.get());

            Thread.sleep(1000);

            System.out.println("Owner Balance: " + state.getBalanceOf(operatorAddress).toString());
            System.out.println("New Account Balance: " + state.getBalanceOf(accountToTransferTo).toString());
        }
    }
}
