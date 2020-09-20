package com.hedera.hashgraph.stablecoin.generator;

import com.hedera.hashgraph.sdk.account.AccountId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.sdk.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.sdk.Transaction;
import com.hedera.hashgraph.stablecoin.sdk.TransferTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Generator {
    final Dotenv env = Dotenv.configure().ignoreIfMissing().load();

    final Random random = new Random();

    final List<com.hedera.hashgraph.stablecoin.sdk.Transaction> transactionsRead = new ArrayList<>();

    public int count = 0;

    File file;

    DataOutputStream writer;

    Ed25519PrivateKey operatorKey;

    AccountId operatorId;

    String tokenName;

    String tokenSymbol;

    int tokenDecimal;

    BigInteger totalSupply;

    Ed25519PrivateKey supplyManager;

    Ed25519PrivateKey complianceManager;

    Ed25519PrivateKey enforcementManager;

    ArrayList<Ed25519PrivateKey> accounts = new ArrayList<>();

    public Generator() throws FileNotFoundException {
        loadEnvironmentVariables();
    }

    public static void main(String[] args) throws IOException {
        new Generator().run();
    }

    String loadEnvironmentVariable(String s) {
        return Objects.requireNonNull(env.get(s), "missing environment variable " + s);
    }

    void loadEnvironmentVariables() throws FileNotFoundException {
        file = new File(loadEnvironmentVariable("HSC_GENERATE_FILE"));
        writer = new DataOutputStream(new FileOutputStream(file));
        operatorKey = Ed25519PrivateKey.fromString(loadEnvironmentVariable("HSC_OPERATOR_KEY"));
        operatorId = AccountId.fromString(loadEnvironmentVariable("HSC_OPERATOR_ID"));
        tokenName = loadEnvironmentVariable("HSC_TOKEN_NAME");
        tokenSymbol = loadEnvironmentVariable("HSC_TOKEN_SYMBOL");
        tokenDecimal = Integer.parseInt(loadEnvironmentVariable("HSC_TOKEN_DECIMAL"));
        totalSupply = new BigInteger(loadEnvironmentVariable("HSC_TOTAL_SUPPLY"));
        supplyManager = Ed25519PrivateKey.fromString(loadEnvironmentVariable("HSC_SUPPLY_MANAGER_KEY"));
        complianceManager = Ed25519PrivateKey.fromString(loadEnvironmentVariable("HSC_COMPLIANCE_MANAGER_KEY"));
        enforcementManager = Ed25519PrivateKey.fromString(loadEnvironmentVariable("HSC_ENFORCEMENT_MANAGER_KEY"));
        count = Integer.parseInt(loadEnvironmentVariable("HSC_TRANSACTION_COUNT"));
    }

    void generateAccounts() throws IOException {
        for (int i = 0; i < 10; ++i) {
            accounts.add(Ed25519PrivateKey.generate());
        }

        // Set KYC passed for all generated accounts, and transfer 100000 units to that account from the supplyManager.
        for (var account : accounts) {
            writeTransactionToFile(new SetKycPassedTransaction(operatorId.account, operatorKey, new Address(account)));
            writeTransactionToFile(new TransferTransaction(operatorId.account, supplyManager, new Address(account), BigInteger.valueOf(100000)));
        }
    }

    void constructor() throws IOException {
        writeTransactionToFile(new ConstructTransaction(
            operatorId.account,
            operatorKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            new Address(supplyManager),
            new Address(complianceManager),
            new Address(enforcementManager)
        ));
    }

    void randomTransfers() throws IOException {
        // Write `HSC_TRANSACTION_COUNT` number of TransferTransactions to the file
        // These TransferTransactions will be random as to from which account, to which account, and amount will
        // be between [0, 100).
        for (int i = 0; i < count; ++i) {
            var from = accounts.get(random.nextInt(accounts.size()));
            var to = accounts.get(random.nextInt(accounts.size()));

            if (from == to) {
                i--;
                continue;
            }
            writeTransactionToFile(new TransferTransaction(
                operatorId.account,
                from,
                new Address(to),
                BigInteger.valueOf(random.nextInt(100))
            ));
        }
    }

    void writeTransactionToFile(Transaction transaction) throws IOException {
        var bytes = transaction.toByteArray();
        writer.writeInt(bytes.length);
        writer.write(bytes);
    }

    public void run() throws IOException {
        // Create and write the ConstructTransaction to file
        constructor();

        // Generate 10 accounts
        generateAccounts();

        // Create and write transfers from a random account to another random account
        randomTransfers();
    }
}
