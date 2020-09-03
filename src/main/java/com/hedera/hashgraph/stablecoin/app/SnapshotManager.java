package com.hedera.hashgraph.stablecoin.app;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.stablecoin.app.proto.AddressEntry;
import com.hedera.hashgraph.stablecoin.app.proto.AllowanceEntry;
import com.hedera.hashgraph.stablecoin.app.proto.Snapshot;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.github.cdimascio.dotenv.Dotenv;

import javax.annotation.Nullable;
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

        // only null on an invalid path
        assert snapshotFiles != null;

        // sort
        Arrays.sort(snapshotFiles);

        // read the first one
        if (snapshotFiles.length > 0) {
            read(snapshotFiles[0]);
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

            for (var addressEntry : snapshot.getAddressesList()) {
                var address = new Address(addressEntry.getAddress());
                var publicKey = address.publicKey;
                var balance = new BigInteger(addressEntry.getBalance().toByteArray());
                var flags = addressEntry.getFlags();
                var isFrozen = (flags & 0x01) != 0;
                var isKycPassed = (flags & 0x02) != 0;

                state.balances.put(publicKey, balance);
                if (isFrozen) state.frozen.put(publicKey, true);
                if (isKycPassed) state.kycPassed.put(publicKey, true);
            }

            for (var allowance : snapshot.getAllowancesList()) {
                state.allowances.put(
                    new AbstractMap.SimpleImmutableEntry<>(
                        PublicKey.fromBytes(allowance.getAddress().toByteArray()),
                        PublicKey.fromBytes(allowance.getOtherAddress().toByteArray())
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
            var address = entry.getKey();
            var flags = 0;

            if (state.frozen.getOrDefault(address, false)) {
                flags |= 0x01;
            }

            if (state.kycPassed.getOrDefault(address, false)) {
                flags |= 0x02;
            }

            snapshot.addAddresses(AddressEntry.newBuilder()
                .setAddress(ByteString.copyFrom(address.toBytes()))
                .setBalance(ByteString.copyFrom(entry.getValue().toByteArray()))
                .setFlags(flags));
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
        // list files and delete state files if there are more than X
    }

    private Path getFilename(Instant timestamp) {
        return stateDir.resolve("" + ChronoUnit.NANOS.between(Instant.EPOCH, timestamp) + ".bin");
    }
}
