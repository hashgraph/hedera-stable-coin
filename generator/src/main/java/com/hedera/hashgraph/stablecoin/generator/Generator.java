package com.hedera.hashgraph.stablecoin.generator;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.stablecoin.Address;
import com.hedera.hashgraph.stablecoin.transaction.ConstructTransaction;
import com.hedera.hashgraph.stablecoin.transaction.SetKycPassedTransaction;
import com.hedera.hashgraph.stablecoin.transaction.Transaction;
import com.hedera.hashgraph.stablecoin.transaction.TransferTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class Generator {
    final Dotenv env = Dotenv.configure().ignoreIfMissing().load();
    final Random random = new Random();

    File file;
    DataOutputStream writer;

    PrivateKey operatorKey;
    String tokenName;
    String tokenSymbol;
    BigInteger tokenDecimal;
    BigInteger totalSupply;
    PrivateKey supplyManager;
    PrivateKey assetProtectionManager;
    int count;

    ArrayList<PrivateKey> accounts;

    Generator() {
    }

    String loadEnvironmentVariable(String s) {
        return Objects.requireNonNull(env.get(s), "missing environment variable " + s);
    }

    void loadEnvironmentVariables() throws FileNotFoundException {
        file = new File(loadEnvironmentVariable("HSC_GENERATE_FILE"));
        writer = new DataOutputStream(new FileOutputStream(file));
        operatorKey = PrivateKey.fromString(loadEnvironmentVariable("HSC_OPERATOR_ID"));
        tokenName = loadEnvironmentVariable("HSC_TOKEN_NAME");
        tokenSymbol = loadEnvironmentVariable("HSC_TOKEN_SYMBOL");
        tokenDecimal = new BigInteger(loadEnvironmentVariable("HSC_TOKEN_DECIMAL"));
        totalSupply = new BigInteger(loadEnvironmentVariable("HSC_TOTAL_SUPPLY"));
        supplyManager = PrivateKey.fromString(loadEnvironmentVariable("HSC_SUPPLY_MANAGER"));
        assetProtectionManager = PrivateKey.fromString(loadEnvironmentVariable("HSC_ASSET_PROTECTION_MANAGER"));
        count = Integer.parseInt(loadEnvironmentVariable("HSC_TRANSACTION_COUNT"));
    }

    void generateAccounts() throws IOException {
        for (int i = 0; i < 10; ++i) {
            accounts.add(PrivateKey.generate());
        }

        // Set KYC passed for all generated accounts, and transfer 100000 units to that account from the supplyManager.
        for (var account: accounts) {
            writeTransactionToFile(new SetKycPassedTransaction(operatorKey, new Address(account.getPublicKey())));
            writeTransactionToFile(new TransferTransaction(supplyManager, new Address(account.getPublicKey()), BigInteger.valueOf(100000)));
        }
    }

    void constructor() throws IOException {
        writeTransactionToFile(new ConstructTransaction(
            operatorKey,
            tokenName,
            tokenSymbol,
            tokenDecimal,
            totalSupply,
            new Address(supplyManager.getPublicKey()),
            new Address(assetProtectionManager.getPublicKey())
        ));
    }

    void randomTransfers() throws IOException {
        // Write `HSC_TRANSACTION_COUNT` number of TransferTransactions to the file
        // These TransferTransactions will be random as to from which account, to which account, and amount will
        // be between [0, 100).
        for (int i = 0; i < count; ++i) {
            var from = accounts.get(random.nextInt() % accounts.size());
            var to = accounts.get(random.nextInt() % accounts.size());

            if (from == to) {
                i--;
                continue;
            }
            writeTransactionToFile(new TransferTransaction(
                from,
                new Address(to.getPublicKey()),
                BigInteger.valueOf(Math.abs(random.nextInt()) % 100)
            ));
        }
    }

    void writeTransactionToFile(Transaction transaction) throws IOException {
        var bytes = transaction.toByteArray();
        writer.writeInt(bytes.length);
        writer.write(bytes);
    }
    
    void run() throws IOException {
        // Load environment vaariables into class fields
        loadEnvironmentVariables();

        // Create and write the ConstructTransaction to file
        constructor();

        // Generate 10 accounts
        generateAccounts();

        // Create and write transfers from a random account to another random account
        randomTransfers();
    }

    static void main(String[] args) throws IOException {
        new Generator().run();
    }
}
