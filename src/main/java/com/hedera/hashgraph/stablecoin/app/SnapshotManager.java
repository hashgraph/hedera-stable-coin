package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.stablecoin.app.proto.AllowanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.BalanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.FrozenEntry;
import com.hedera.hashgraph.stablecoin.app.proto.KycPassedEntry;
import com.hedera.hashgraph.stablecoin.app.proto.Snapshot;
import com.hedera.hashgraph.stablecoin.sdk.Address;
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

            for (var balanceEntry : snapshot.getBalancesList()) {
                var address = new Address(balanceEntry.getAddress());
                var balance = new BigInteger(balanceEntry.getBalance().toByteArray());

                state.balances.put(address.publicKey, balance);
            }

            for (var kycPassedEntry : snapshot.getKycPassedList()) {
                var address = new Address(kycPassedEntry.getAddress());
                var isKycPassed = kycPassedEntry.getIsKycPassed();

                if (isKycPassed) {
                    state.kycPassed.put(address.publicKey, true);
                }
            }

            for (var frozenEntry : snapshot.getFrozenList()) {
                var address = new Address(frozenEntry.getAddress());
                var isFrozen = frozenEntry.getIsFrozen();

                if (isFrozen) {
                    state.frozen.put(address.publicKey, true);
                }
            }

            for (var allowance : snapshot.getAllowancesList()) {
                state.allowances.put(
                    new AbstractMap.SimpleImmutableEntry<>(
                        Ed25519PublicKey.fromBytes(allowance.getAddress().toByteArray()),
                        Ed25519PublicKey.fromBytes(allowance.getOtherAddress().toByteArray())
                    ),
                    new BigInteger(allowance.getAllowance().toByteArray())
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
            .setProposedOwner(ByteString.copyFrom(state.getProposedOwner().publicKey.toBytes()));

        for (var entry : state.balances.entrySet()) {
            snapshot.addBalances(BalanceEntry.newBuilder()
                .setAddress(ByteString.copyFrom(entry.getKey().toBytes()))
                .setBalance(ByteString.copyFrom(entry.getValue().toByteArray())));
        }

        for (var entry : state.kycPassed.entrySet()) {
            snapshot.addKycPassed(KycPassedEntry.newBuilder()
                .setAddress(ByteString.copyFrom(entry.getKey().toBytes()))
                .setIsKycPassed(entry.getValue()));
        }

        for (var entry : state.frozen.entrySet()) {
            snapshot.addFrozen(FrozenEntry.newBuilder()
                .setAddress(ByteString.copyFrom(entry.getKey().toBytes()))
                .setIsFrozen(entry.getValue()));
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
