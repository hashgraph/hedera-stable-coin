package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.stablecoin.app.proto.AllowanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.BalanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.ExternalAllowanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.FrozenEntry;
import com.hedera.hashgraph.stablecoin.app.proto.KycPassedEntry;
import com.hedera.hashgraph.stablecoin.app.proto.Snapshot;
import com.hedera.hashgraph.stablecoin.app.proto.TransactionReceiptEntry;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import com.hedera.hashgraph.stablecoin.sdk.TransactionId;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.Arrays;

public final class SnapshotManager {
    private final State state;

    private final Path stateDir;

    private final int stateHistorySize;

    public SnapshotManager(Dotenv env, State state) throws IOException {
        this.state = state;

        // resolve a location to store our snapshots
        stateDir = Paths.get(env.get("HSC_STATE_DIR", "state"));

        // create directories if they do not exist
        Files.createDirectories(stateDir);

        // number of state files to keep
        stateHistorySize = Integer.parseInt(env.get("HSC_STATE_HISTORY_SIZE", "10"));
    }

    public void tryReadLatest() throws IOException {
        var snapshotFiles = stateDir.toFile().listFiles();
        assert snapshotFiles != null;

        // sort
        Arrays.sort(snapshotFiles);

        // read the greatest one
        if (snapshotFiles.length > 0) {
            read(snapshotFiles[snapshotFiles.length - 1]);
        }
    }

    public void read(File stateFile) throws IOException {
        // extract and parse timestamp from state file
        var stateBaseName = com.google.common.io.Files.getNameWithoutExtension(stateFile.getName());
        var stateTimestamp = Instant.ofEpochSecond(0, Long.parseLong(stateBaseName));

        // read in and parse the file into the state protobuf
        var snapshot = Snapshot.parseFrom(Files.readAllBytes(stateFile.toPath()));

        state.lock();

        try {
            state.setTimestamp(stateTimestamp);
            state.setTokenName(snapshot.getTokenName());
            state.setTokenSymbol(snapshot.getTokenSymbol());
            state.setTokenDecimal(snapshot.getTokenDecimal());
            state.setTotalSupply(new BigInteger(snapshot.getTotalSupply().toByteArray()));
            state.setOwner(new Address(snapshot.getOwner()));
            state.setProposedOwner(new Address(snapshot.getProposedOwner()));
            state.setComplianceManager(new Address(snapshot.getComplianceManager()));
            state.setSupplyManager(new Address(snapshot.getSupplyManager()));
            state.setEnforcementManager(new Address(snapshot.getEnforcementManager()));

            state.transactionReceipts.clear();

            for (var receiptEntry : snapshot.getTransactionReceiptsList()) {
                var validStart = Instant.ofEpochSecond(0, receiptEntry.getValidStart());
                var consensus = Instant.ofEpochSecond(0, receiptEntry.getConsensus());
                var address = new Address(receiptEntry.getAddress());
                var status = Status.valueOf(receiptEntry.getStatus());

                var id = new TransactionId(address, validStart);
                var receipt = new TransactionReceipt(consensus, id, status);

                state.transactionReceipts.put(id, receipt);
            }

            state.balances.clear();

            for (var balanceEntry : snapshot.getBalancesList()) {
                var address = new Address(balanceEntry.getAddress());
                var balance = new BigInteger(balanceEntry.getBalance().toByteArray());

                state.increaseBalanceOf(address, balance);
            }

            state.kycPassed.clear();

            for (var kycPassedEntry : snapshot.getKycPassedList()) {
                state.setKycPassed(new Address(kycPassedEntry.getAddress()));
            }

            state.frozen.clear();

            for (var frozenEntry : snapshot.getFrozenList()) {
                state.freeze(new Address(frozenEntry.getAddress()));
            }

            state.allowances.clear();

            for (var allowance : snapshot.getAllowancesList()) {
                state.allowances.put(
                    new AbstractMap.SimpleImmutableEntry<>(
                        Ed25519PublicKey.fromBytes(allowance.getAddress().toByteArray()),
                        Ed25519PublicKey.fromBytes(allowance.getOtherAddress().toByteArray())
                    ),
                    new BigInteger(allowance.getAllowance().toByteArray())
                );
            }

            state.externalAllowances.clear();

            for (var externalAllowance : snapshot.getExternalAllowancesList()) {
                state.externalAllowances.put(
                    new Tuple3(
                        Ed25519PublicKey.fromBytes(externalAllowance.getAddress().toByteArray()),
                        externalAllowance.getNetworkURI(),
                        externalAllowance.getOtherAddress().toByteArray()
                    ),
                    new BigInteger(externalAllowance.getAllowance().toByteArray())
                );
            }
        } finally {
            state.unlock();
        }
    }

    void write() throws IOException {
        var stateFilename = getFilename(state.getTimestamp());

        var snapshot = Snapshot.newBuilder()
            .setTokenName(state.getTokenName())
            .setTokenSymbol(state.getTokenSymbol())
            .setTokenDecimal(state.getTokenDecimal())
            .setTotalSupply(ByteString.copyFrom(state.getTotalSupply().toByteArray()))
            .setOwner(ByteString.copyFrom(state.getOwner().publicKey.toBytes()))
            .setSupplyManager(ByteString.copyFrom(state.getSupplyManager().publicKey.toBytes()))
            .setComplianceManager(ByteString.copyFrom(state.getComplianceManager().publicKey.toBytes()))
            .setProposedOwner(ByteString.copyFrom(state.getProposedOwner().publicKey.toBytes()))
            .setEnforcementManager(ByteString.copyFrom(state.getEnforcementManager().toBytes()));

        for (var transactionReceipt : state.transactionReceipts.values()) {
            var transactionId = transactionReceipt.transactionId;

            snapshot.addTransactionReceipts(TransactionReceiptEntry.newBuilder()
                .setStatus(transactionReceipt.status.getValue())
                .setConsensus(ChronoUnit.NANOS.between(Instant.EPOCH, transactionReceipt.consensusAt))
                .setAddress(ByteString.copyFrom(transactionId.address.toBytes()))
                .setValidStart(ChronoUnit.NANOS.between(Instant.EPOCH, transactionId.validStart)));
        }

        for (var entry : state.balances.entrySet()) {
            snapshot.addBalances(BalanceEntry.newBuilder()
                .setAddress(ByteString.copyFrom(entry.getKey().toBytes()))
                .setBalance(ByteString.copyFrom(entry.getValue().toByteArray())));
        }

        for (var address : state.kycPassed) {
            snapshot.addKycPassed(KycPassedEntry.newBuilder()
                .setAddress(ByteString.copyFrom(address.toBytes())));
        }

        for (var address : state.frozen) {
            snapshot.addFrozen(FrozenEntry.newBuilder()
                .setAddress(ByteString.copyFrom(address.toBytes())));
        }

        for (var entry : state.allowances.entrySet()) {
            var key = entry.getKey();
            var address = key.getKey();
            var otherAddress = key.getValue();
            var allowance = entry.getValue();

            if (allowance.equals(BigInteger.ZERO)) {
                continue;
            }

            snapshot.addAllowances(AllowanceEntry.newBuilder()
                .setAddress(ByteString.copyFrom(address.toBytes()))
                .setOtherAddress(ByteString.copyFrom(otherAddress.toBytes()))
                .setAllowance(ByteString.copyFrom(allowance.toByteArray()))
                .build());
        }

        for (var entry : state.externalAllowances.entrySet()) {
            var key = (Tuple3) entry.getKey();
            var address = key.first;
            var otherAddress = key.third;
            var networkUri = key.second;
            var allowance = entry.getValue();

            if (allowance.equals(BigInteger.ZERO)) {
                continue;
            }

            snapshot.addExternalAllowances(ExternalAllowanceEntry.newBuilder()
                .setAddress(ByteString.copyFrom(address.toBytes()))
                .setOtherAddress(ByteString.copyFrom(otherAddress))
                .setNetworkURI(networkUri)
                .setAllowance(ByteString.copyFrom(allowance.toByteArray()))
                .build());
        }

        var snapshotBytes = snapshot.build().toByteArray();

        Files.write(stateFilename, snapshotBytes,
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    void prunePrevious() {
        var snapshotFiles = stateDir.toFile().listFiles();
        assert snapshotFiles != null;

        Arrays.sort(snapshotFiles);

        if (snapshotFiles.length > stateHistorySize) {
            for (var i = 0; i < (snapshotFiles.length - stateHistorySize); i++) {
                // noinspection ResultOfMethodCallIgnored
                snapshotFiles[i].delete();
            }
        }
    }

    private Path getFilename(Instant timestamp) {
        return stateDir.resolve("" + ChronoUnit.NANOS.between(Instant.EPOCH, timestamp) + ".bin");
    }
}
