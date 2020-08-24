package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaPreCheckStatusException;
import com.hedera.hashgraph.sdk.HederaReceiptStatusException;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.TopicCreateTransaction;
import com.hedera.hashgraph.sdk.TopicId;
import com.hedera.hashgraph.sdk.TopicMessageSubmitTransaction;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class App {
    // access to the system environment overlaid with a .env file, if present
    final Dotenv env = Dotenv.configure().ignoreIfMissing().load();

    // client to the Hedera(tm) Hashgraph network
    // used when submitting transactions to hedera and to listen
    // for messages from the mirror node
    final Client hederaClient = createHederaClient();

    // handle to the vert.x event loop
    // used for interaction with postgresql and to setup API servers
    final Vertx vertx = Vertx.vertx();

    // current state of the token contract
    final State contractState = new State();

    // topic ID of the contract instance
    final TopicId topicId = getOrCreateContractInstance();

    // listener to the Hedera topic
    final TopicListener topicListener = new TopicListener(contractState, hederaClient, topicId);

    // verticle providing the read-only contract state API
    final StateVerticle stateVerticle = new StateVerticle(contractState);

    @SuppressWarnings("CheckedExceptionNotThrown") // false positive in errorprone
    private App() throws InterruptedException, TimeoutException, HederaReceiptStatusException, HederaPreCheckStatusException {
    }

    public static void main(String[] args) throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException, InterruptedException {
        var app = new App();

        // start listening to the contract instance (topic) on the Hedera
        // mirror node
        app.startListeningOnTopic();

        // expose the read-only API for the contract state
        app.deployStateVerticle();

        // wait while the APIs and the topic listener run in the background
        // todo: listen to SIGINT/SIGTERM to cleanly exit
        while (true) Thread.sleep(0);
    }

    Client createHederaClient() {
        // TODO: we need an .env variable to allow switching network
        // TODO: ..3 is commented out as its currently (as of 8-23) down
        var network = new HashMap<AccountId, String>();
        // network.put(new AccountId(3), "0.testnet.hedera.com:50211");
        network.put(new AccountId(4), "1.testnet.hedera.com:50211");
        network.put(new AccountId(5), "2.testnet.hedera.com:50211");
        network.put(new AccountId(6), "3.testnet.hedera.com:50211");

        var client = Client.forNetwork(network);
        client.setMirrorNetwork(List.of("hcs.testnet.mirrornode.hedera.com:5600"));

        // if an OPERATOR_ID and OPERATOR_KEY were provided, set them
        // on the client; this is only needed when we are creating a new
        // contract instance

        var operatorIdVar = env.get("HSC_OPERATOR_ID");
        var operatorKeyVar = env.get("HSC_OPERATOR_KEY");

        if (operatorIdVar != null && operatorKeyVar != null) {
            client.setOperator(
                AccountId.fromString(operatorIdVar),
                PrivateKey.fromString(operatorKeyVar));
        }

        return client;
    }

    TopicId createContractInstance() throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException, InterruptedException {
        // if we were not given a topic ID
        // we need to create a new topic, and send a construct message,
        // which establishes our operator as the owner

        var operatorPrivateKey = PrivateKey.fromString(Objects.requireNonNull(
            env.get("HSC_OPERATOR_KEY"), "missing environment variable HSC_OPERATOR_KEY"));

        var tokenName = Objects.requireNonNull(
            env.get("HSC_TOKEN_NAME"), "missing environment variable HSC_TOKEN_NAME");

        var tokenSymbol = Objects.requireNonNull(
            env.get("HSC_TOKEN_SYMBOL"), "missing environment variable HSC_TOKEN_SYMBOL");

        var tokenDecimal = new BigInteger(Objects.requireNonNull(
            env.get("HSC_TOKEN_DECIMAL"), "missing environment variable HSC_TOKEN_DECIMAL"));

        var totalSupply = new BigInteger(Objects.requireNonNull(
            env.get("HSC_TOTAL_SUPPLY"), "missing environment variable HSC_TOTAL_SUPPLY"));

        var ownerKey = Optional.ofNullable(env.get("HSC_OWNER_KEY"))
            .map(PrivateKey::fromString);

        var supplyManagerAddress = Optional.ofNullable(env.get("HSC_SUPPLY_MANAGER_KEY"))
            .map(PublicKey::fromString).map(Address::new);

        var assetProtectionManagerAddress = Optional.ofNullable(env.get("HSC_ASSET_PROTECTION_MANAGER_KEY"))
            .map(PublicKey::fromString).map(Address::new);

        // create the new topic ID
        var topicId = Optional.ofNullable(new TopicCreateTransaction()
            .execute(hederaClient)
            .transactionId
            .getReceipt(hederaClient)
            .topicId);

        // after a topic create transaction, this will be set
        // or we would have died elsewhere
        assert topicId.isPresent();

        System.out.println("created topic " + topicId.get());

        // now we need to create a <Construct> transaction
        var operatorAddress = new Address(operatorPrivateKey.getPublicKey());

        var constructTransactionBytes = new ConstructTransaction(
            ownerKey.orElse(operatorPrivateKey),
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            supplyManagerAddress.orElse(operatorAddress),
            assetProtectionManagerAddress.orElse(operatorAddress)
        ).toByteArray();

        // and finally submit it

        new TopicMessageSubmitTransaction()
            .setTopicId(topicId.get())
            .setMessage(constructTransactionBytes)
            .execute(hederaClient)
            .transactionId
            .getReceipt(hederaClient);

        // wait 10s because subscribe retries on the SDK v2 are not working
        // so we need to wait until the topic is fully established on the mirror node
        Thread.sleep(10_000);

        return topicId.get();
    }

    TopicId getOrCreateContractInstance() throws HederaReceiptStatusException, TimeoutException, HederaPreCheckStatusException, InterruptedException {
        @Var var maybeTopicId = Optional.ofNullable(env.get("HSC_TOPIC_ID")).map(TopicId::fromString);

        if (maybeTopicId.isPresent()) {
            return maybeTopicId.get();
        }

        // no topic ID is found in the system environment
        System.out.println("no Topic ID found, creating a new topic ...");

        return createContractInstance();
    }

    void startListeningOnTopic() {
        topicListener.startListening();
    }

    void deployStateVerticle() {
        DeploymentOptions deploymentOptions = new DeploymentOptions()
            // TODO: the port for this API should be configurable
            .setConfig(new JsonObject().put("HTTP_PORT", 9000));

        vertx.deployVerticle(stateVerticle, deploymentOptions);
    }


    /*private static void runBenchmark() throws IOException, HederaReceiptStatusException, TimeoutException, HederaPreCheckStatusException {
        var state = new State();

        var generatedFile = new File(loadEnvironmentVariable("HSC_GENERATE_FILE"));

        var count = Integer.parseInt(loadEnvironmentVariable("HSC_TRANSACTION_COUNT"));

        var topicId = TopicId.fromString("0.0.5005");

        Benchmark.runBenchmark(state, client, generatedFile, count, topicId, operatorKey);
    }*/
}
