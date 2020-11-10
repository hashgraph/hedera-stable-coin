package com.hedera.hashgraph.stablecoin.app;

import com.google.errorprone.annotations.Var;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.consensus.ConsensusMessageSubmitTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicCreateTransaction;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.sdk.mirror.MirrorClient;
import com.hedera.hashgraph.stablecoin.app.api.ApiVerticle;
import com.hedera.hashgraph.stablecoin.app.repository.TransactionRepository;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import io.github.cdimascio.dotenv.Dotenv;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class App {
    // access to the system environment overlaid with a .env file, if present
    final Dotenv env = Dotenv.configure().ignoreIfMissing().load();

    // client to the Hedera(tm) Hashgraph network
    // used when submitting transactions to hedera
    final Client hederaClient = createHederaClient();

    // client to a Hedera mirror node
    // used to listen for messages from the mirror node
    final MirrorClient mirrorHederaClient = createMirrorHederaClient();

    // handle to the vert.x event loop
    // used for interaction with postgresql and to setup API servers
    final Vertx vertx = Vertx.vertx();

    // current state of the token contract
    final State contractState = new State();

    // SQL connection manager
    final SqlConnectionManager connectionManager = new SqlConnectionManager(env);

    final PgPool pgPool = createPgPool();

    // repository for interaction with transactions in the database
    final TransactionRepository transactionRepository = new TransactionRepository(connectionManager);

    // state snapshot manager
    // responsible for reading and writing state snapshots on a state interval
    final SnapshotManager snapshotManager = new SnapshotManager(env, contractState);

    // topic ID of the contract instance
    final ConsensusTopicId topicId = getOrCreateContractInstance();

    // listener to the Hedera topic
    final TopicListener topicListener = new TopicListener(contractState, mirrorHederaClient, topicId, transactionRepository);

    // on an interval, we commit our state
    final CommitInterval commitInterval = new CommitInterval(
        env,
        contractState,
        transactionRepository,
        snapshotManager
    );

    @SuppressWarnings("CheckedExceptionNotThrown") // false positive in errorprone
    private App() throws IOException, HederaStatusException, InterruptedException {
    }

    public static void main(String[] args) throws InterruptedException, IOException, SQLException, HederaStatusException {
        var app = new App();

        // should add a real arg parser
        if (args.length > 2 && args[1].equals("--bench")) {
            app.runBenchmark(args[2]);
            return;
        }

        if (args.length == 1 && args[0].equals("--newkeys")) {
            System.out.println("");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("Generating keys.");
            System.out.println("----------------------------------------------------------------------");
            Ed25519PrivateKey complianceManager = Ed25519PrivateKey.generate();
            Ed25519PrivateKey supplyManager = Ed25519PrivateKey.generate();
            Ed25519PrivateKey enforcementManager = Ed25519PrivateKey.generate();
            Ed25519PrivateKey ownerKey = Ed25519PrivateKey.generate();
            System.out.println("");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("These are the **public** keys you can paste into the environment files");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("HSC_COMPLIANCE_MANAGER_KEY_PUB=" + supplyManager.publicKey.toString());
            System.out.println("HSC_SUPPLY_MANAGER_KEY_PUB=" + complianceManager.publicKey.toString());
            System.out.println("HSC_ENFORCEMENT_MANAGER_KEY_PUB=" + enforcementManager.publicKey.toString());
            System.out.println("");
            System.out.println("Private key for the owner of the token");
            System.out.println("HSC_OWNER_KEY=" + ownerKey.toString());
            System.out.println("");
            System.out.println("These are the **private** keys you will need to approve operations");
            System.out.println("Compliance manager key=" + supplyManager.toString());
            System.out.println("Supply manager key=" + complianceManager.toString());
            System.out.println("Enforcement manager key=" + enforcementManager.toString());
            System.out.println("");
            System.out.println("----------------------------------------------------------------------");

            System.exit(0);
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

    private String requireEnv(String name) {
        return Objects.requireNonNull(env.get(name), "missing environment variable " + name);
    }

    Client createHederaClient() {
        @Var var client = Client.forTestnet();
        @Var var network = requireEnv("HSC_NETWORK");
        switch (network.toUpperCase()) {
            case "MAINNET":
                client = Client.forMainnet();
                break;
            case "TESTNET":
                client = Client.forTestnet();
                break;
//            case "PREVIEWNET":
//                client = Client.forPreviewnet();
//                break;
            default:
                System.out.println("HSC_NETWORK environment variable not set to mainnet, testnet or previewnet, exiting");
                System.exit(0);
                break;
        }

        // if an OPERATOR_ID and OPERATOR_KEY were provided, set them
        // on the client; this is only needed when we are creating a new
        // contract instance

        var operatorIdVar = env.get("HSC_OPERATOR_ID");
        var operatorKeyVar = env.get("HSC_OPERATOR_KEY");

        if (operatorIdVar != null && operatorKeyVar != null) {
            client.setOperator(
                AccountId.fromString(operatorIdVar),
                Ed25519PrivateKey.fromString(operatorKeyVar));
        }

        return client;
    }

    MirrorClient createMirrorHederaClient() {
        return new MirrorClient("hcs.testnet.mirrornode.hedera.com:5600");
        // return new MirrorClient("testnet.api.kabuto.sh:50211");
    }

    PgPool createPgPool() {
        return PgPool.pool(
            PgConnectOptions.fromUri(requireEnv("HSC_DATABASE_URL"))
                .setUser(requireEnv("HSC_DATABASE_USERNAME"))
                .setPassword(requireEnv("HSC_DATABASE_PASSWORD")),
            new PoolOptions()
                .setMaxSize(10)
        );
    }

    ConsensusTopicId createContractInstance() throws HederaStatusException, InterruptedException {
        // if we were not given a topic ID
        // we need to create a new topic, and send a construct message,
        // which establishes our operator as the owner

        System.out.println("creating topic");
        var operatorId = AccountId.fromString(requireEnv("HSC_OPERATOR_ID"));
        var operatorPrivateKey = Ed25519PrivateKey.fromString(requireEnv("HSC_OPERATOR_KEY"));
        var tokenName = requireEnv("HSC_TOKEN_NAME");
        var tokenSymbol = requireEnv("HSC_TOKEN_SYMBOL");
        var tokenDecimal = Integer.parseInt(requireEnv("HSC_TOKEN_DECIMAL"));
        var totalSupply = new BigInteger(requireEnv("HSC_TOTAL_SUPPLY"));

        var ownerKey = Optional.ofNullable(env.get("HSC_OWNER_KEY"))
            .map(Ed25519PrivateKey::fromString);

        var supplyManagerKey = Optional.ofNullable(env.get("HSC_SUPPLY_MANAGER_KEY_PUB"))
            .map(Ed25519PublicKey::fromString);

        var complianceManagerKey = Optional.ofNullable(env.get("HSC_COMPLIANCE_MANAGER_KEY_PUB"))
            .map(Ed25519PublicKey::fromString);

        var enforcementManagerKey = Optional.ofNullable(env.get("HSC_ENFORCEMENT_MANAGER_KEY_PUB"))
            .map(Ed25519PublicKey::fromString);

        // create the new topic ID
        var topicId = new ConsensusTopicCreateTransaction()
            .execute(hederaClient)
            .getReceipt(hederaClient)
            .getConsensusTopicId();

        System.out.println("created topic " + topicId);

        // now we need to create a <Construct> transaction
        System.out.println("creating contract for token");

        var constructTransactionBytes = new ConstructTransaction(
            operatorId.account,
            ownerKey.orElse(operatorPrivateKey),
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            new Address(supplyManagerKey.orElse(operatorPrivateKey.publicKey)),
            new Address(complianceManagerKey.orElse(operatorPrivateKey.publicKey)),
            new Address(enforcementManagerKey.orElse(operatorPrivateKey.publicKey))
        ).toByteArray();

        // and finally submit it

        new ConsensusMessageSubmitTransaction()
            .setTopicId(topicId)
            .setMessage(constructTransactionBytes)
            .execute(hederaClient)
            .getReceipt(hederaClient);

        System.out.println("pausing 10s");

        // wait 10s because subscribe retries on the SDK v2 are not working
        // so we need to wait until the topic is fully established on the mirror node
        for (int i=0; i < 10; i++) {
            System.out.print(".");
            Thread.sleep(1_000);
        }
        System.out.println("");

        return topicId;
    }

    ConsensusTopicId getOrCreateContractInstance() throws InterruptedException, HederaStatusException {
        @Var var maybeTopicId = Optional.ofNullable(env.get("HSC_TOPIC_ID")).filter(topic -> ! topic.isEmpty()).map(ConsensusTopicId::fromString);

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
        var httpPort = Optional.ofNullable(env.get("HSC_STATE_PORT")).map(Integer::parseInt).orElse(9000);
        DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setInstances(8)
            // the port for this API should be configurable
            .setConfig(new JsonObject().put("HTTP_PORT", httpPort));

        vertx.deployVerticle(() -> new ApiVerticle(contractState, pgPool, transactionRepository), deploymentOptions);
        System.out.println("Listening on port : " + httpPort);
    }

    void runBenchmark(String inputFile) throws IOException, SQLException {
        new Benchmark(
            contractState,
            transactionRepository,
            new File(inputFile)
        ).run();
    }
}
