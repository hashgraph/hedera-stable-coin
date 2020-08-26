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
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class App {
    final static Dotenv env = Dotenv.configure().ignoreIfMissing().load();
    static PrivateKey operatorKey = PrivateKey.fromString(loadEnvironmentVariable("HSC_OPERATOR_KEY"));
    static AccountId operatorId = AccountId.fromString(loadEnvironmentVariable("HSC_OPERATOR_ID"));
    // configure a client to connect to Hedera
    // todo: we need an .env variable to allow switching network
    static Client client = Client.forTestnet();

    private App() {
    }

    public static void main(String[] args) throws TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException, InterruptedException, IOException {
        setClient();

        showMenu();
    }

    @SuppressWarnings("ReturnMissingNullable")
    static String loadEnvironmentVariable(String s) {
        return Objects.requireNonNull(env.get(s), "missing environment variable " + s);
    }

    static void setClient() {
        // configure the client operator
        client.setOperator(operatorId, operatorKey);
    }

    static void showMenu() throws InterruptedException, TimeoutException, HederaReceiptStatusException, HederaPreCheckStatusException, IOException {
        @Var
        var selection = 0;

        while (selection != 1 && selection != 2) {
            selection =  menu();
        }

        if (selection == 1) {
            runApp();
        }
        if (selection == 2) {
            runBenchmark();
        }
    }

    public static int menu() {
        Scanner input = new Scanner(System.in, Charset.defaultCharset().name());

        System.out.println("1 - Run App");
        System.out.println("2 - Run Benchmark");

        return  input.nextInt();
    }

    static void runApp() throws IOException, InterruptedException, TimeoutException, HederaPreCheckStatusException, HederaReceiptStatusException {
        Vertx vertx = Vertx.vertx();

        @Var var maybeTopicId = Optional.ofNullable(env.get("HSC_TOPIC_ID")).map(TopicId::fromString);

        var operatorAddress = new Address(operatorKey.getPublicKey());

        var file = new File("state.bin");

        if (maybeTopicId.isEmpty()) {
            // if we were not given a topic ID
            // we need to create a new topic, and send a construct message,
            // which establishes our operator as the owner

            // we need the token properties to do this
            var tokenName = loadEnvironmentVariable("HSC_TOKEN_NAME");

            var tokenSymbol = loadEnvironmentVariable("HSC_TOKEN_SYMBOL");

            var tokenDecimal = new BigInteger(loadEnvironmentVariable("HSC_TOKEN_DECIMAL"));

            var totalSupply = new BigInteger(loadEnvironmentVariable("HSC_TOTAL_SUPPLY"));

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
        var state = State.tryFromFile(file);

        // create a new topic listener, and start listening
        new TopicListener(state, client, topicId).startListening();

        // create StateVerticle
        startApi(vertx, state);

        // listen to Api and get name (use for testing)
        // listenToApi(vertx);

        // wait while the APIs and the topic listener run in the background
        // todo: listen to SIGINT/SIGTERM to cleanly exit
        while (true) Thread.sleep(0);
    }

    static void startApi(Vertx vertx, State state) throws IOException {
        ServerSocket socket = new ServerSocket(0);

        var port = socket.getLocalPort();
        socket.close();

        DeploymentOptions options = new DeploymentOptions()
            .setConfig(new JsonObject().put("HTTP_PORT", port));

        StateVerticle stateVerticle = new StateVerticle(state);

        vertx.deployVerticle(stateVerticle, options);
    }

    // Use to test StateVerticle
    @SuppressWarnings("NullableDereference")
    static void listenToApi(Vertx vertx) {
        WebClientOptions wcOptions = new WebClientOptions()
            .setUserAgent("My-App/1.2.3");
        wcOptions.setKeepAlive(false);
        WebClient webClient = WebClient.create(vertx, wcOptions);

        webClient
            .get(8080, "localhost", "/api/tokenname")
            .send(ar -> {
                HttpResponse<Buffer> response = ar.result();
                if (response != null && response.body() != null) {
                    System.out.println(response.body().toString());
                }
            });
    }

    private static void runBenchmark() throws IOException, HederaReceiptStatusException, TimeoutException, HederaPreCheckStatusException {
        var state = new State();

        var generatedFile = new File(loadEnvironmentVariable("HSC_GENERATE_FILE"));

        var count = Integer.parseInt(loadEnvironmentVariable("HSC_TRANSACTION_COUNT"));

        var topicId = TopicId.fromString("0.0.5005");

        Benchmark.runBenchmark(state, client, generatedFile, count, topicId, operatorKey);
    }
}
