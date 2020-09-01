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
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
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

    // SQL connection manager
    final SqlConnectionManager connectionManager = new SqlConnectionManager(env);

    // repository for interaction with transactions in the database
    final TransactionRepository transactionRepository = new TransactionRepository(connectionManager);

    // state snapshot manager
    // responsible for reading and writing state snapshots on a state interval
    final SnapshotManager snapshotManager = new SnapshotManager(env, contractState);

    // topic ID of the contract instance
    final TopicId topicId = getOrCreateContractInstance();

    // listener to the Hedera topic
    final TopicListener topicListener = new TopicListener(contractState, hederaClient, topicId, transactionRepository);

    // verticle providing the read-only contract state API
    final StateVerticle stateVerticle = new StateVerticle(contractState);

    // on an interval, we commit our state
    final CommitInterval commitInterval = new CommitInterval(
        env,
        contractState,
        transactionRepository,
        snapshotManager
    );

    @SuppressWarnings("CheckedExceptionNotThrown") // false positive in errorprone
    private App() throws InterruptedException, TimeoutException, HederaReceiptStatusException, HederaPreCheckStatusException, IOException {
    }

    public static void main(String[] args) throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException, InterruptedException, IOException, SQLException {
        var app = new App();

        // should add a real arg parser
        if (args.length > 2 && args[1].equals("--bench")) {
            app.runBenchmark(args[2]);
            return;
        }

        // try to read the latest state snapshot
        // this is to let us resume instead of reading from the beginning of history
        app.snapshotManager.tryReadLatest();

        // start listening to the contract instance (topic) on the Hedera
        // mirror node
        app.startListeningOnTopic();

        // expose the read-only API for the contract state
        app.deployStateVerticle();

        // wait while the APIs and the topic listener run in the background
        // should listen to SIGINT/SIGTERM to cleanly exit
        while (true) Thread.sleep(0);
    }

    Client createHederaClient() {
        // we need an .env variable to allow switching network
        var network = new HashMap<AccountId, String>();
        network.put(new AccountId(3), "0.testnet.hedera.com:50211");
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

        var supplyManagerKey = Optional.ofNullable(env.get("HSC_SUPPLY_MANAGER_KEY"))
            .map(PrivateKey::fromString);

        var assetProtectionManagerKey = Optional.ofNullable(env.get("HSC_ASSET_PROTECTION_MANAGER_KEY"))
            .map(PrivateKey::fromString);

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

        var constructTransactionBytes = new ConstructTransaction(
            ownerKey.orElse(operatorPrivateKey),
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            new Address(supplyManagerKey.orElse(operatorPrivateKey).getPublicKey()),
            new Address(assetProtectionManagerKey.orElse(operatorPrivateKey).getPublicKey())
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
            // the port for this API should be configurable
            .setConfig(new JsonObject().put("HTTP_PORT", 9000));

        vertx.deployVerticle(stateVerticle, deploymentOptions);
    }

    void runBenchmark(String inputFile) throws IOException, SQLException {
        new Benchmark(
            contractState,
            hederaClient,
            transactionRepository,
            new File(inputFile)
        ).run();
    }
}
