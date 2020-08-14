package com.hedera.hashgraph.stablecoin;

import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class App {
    private App() {
    }

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

        @Var var maybeTopicId = Optional.ofNullable(env.get("HSC_TOPIC_ID")).map(TopicId::fromString);

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

            System.out.println("created topic " + maybeTopicId.get());

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

            // wait 10s because subscribe retries on the SDK v2 are not working
            // so we need to wait until the topic is fully established on the mirror node
            Thread.sleep(10_000);
        }

        var topicId = maybeTopicId.get();

        // create a new instance of in-memory state
        var state = new State();

        // create a new topic listener, and start listening
        new TopicListener(state, client, topicId).startListening();

        // wait while the APIs and the topic listener run in the background
        // todo: listen to SIGINT/SIGTERM to cleanly exit
        while (true) Thread.sleep(0);
    }
}
