package com.hedera.hashgraph.stablecoin;

import com.google.protobuf.ByteString;
import com.hedera.hashgraph.sdk.PublicKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;

public final class State {
    private final Map<PublicKey, BigInteger> balances = new HashMap<>();
    private final Map<PublicKey, Boolean> frozen = new HashMap<>();
    private final Map<PublicKey, Boolean> kycPassed = new HashMap<>();
    private final Map<SimpleImmutableEntry<PublicKey, PublicKey>, BigInteger> allowances = new HashMap<>();

    /**
     * Display name of the stable coin (ex., Hbar, Ether).
     */
    private String tokenName = "";

    /**
     * Ticker symbol for the stable coin (ex., ETH, USD, etc.).
     */
    private String tokenSymbol = "";

    private BigInteger tokenDecimal = BigInteger.ZERO;

    /**
     * The owner of the stable coin instance. The owner has control over all administrative controls and
     * can re-assign the asset protection manager and supply manager.
     */
    private Address owner = Address.ZERO;

    private Address supplyManager = Address.ZERO;

    private Address assetProtectionManager = Address.ZERO;

    /**
     * The proposed owner may be set at any time by the current owner. The proposed owner can then claim their
     * ownership which will change the owner property.
     */
    private Address proposedOwner = Address.ZERO;

    private BigInteger totalSupply = BigInteger.ZERO;

    private Instant timestamp = Instant.EPOCH;

    public State() {
    }

    public Address getOwner() {
        return owner;
    }

    public String getTokenName() {
        return tokenName;
    }

    /**
     * Set the token display name.
     * May only be called once (during construct).
     */
    public void setTokenName(String tokenName) {
        assert !tokenName.isEmpty();

        this.tokenName = tokenName;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    /**
     * Set the token ticker symbol.
     * May only be called once (during construct).
     */
    public void setTokenSymbol(String tokenSymbol) {
        assert !tokenSymbol.isEmpty();

        this.tokenSymbol = tokenSymbol;
    }

    public BigInteger getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(BigInteger tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public BigInteger getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(BigInteger totalSupply) {
        this.totalSupply = totalSupply;
    }

    public Address getSupplyManager() {
        return supplyManager;
    }

    public void setSupplyManager(Address supplyManager) {
        this.supplyManager = supplyManager;
    }

    public Address getAssetProtectionManager() {
        return assetProtectionManager;
    }

    public void setAssetProtectionManager(Address assetProtectionManager) {
        this.assetProtectionManager = assetProtectionManager;
    }

    public BigInteger getAllowance(Address caller, Address spender) {
        return allowances.getOrDefault(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey), BigInteger.ZERO);
    }

    public BigInteger getBalanceOf(Address address) {
        return balances.getOrDefault(address.publicKey, BigInteger.ZERO);
    }

    public Address getProposedOwner() {
        return proposedOwner;
    }

    public void setProposedOwner(Address address) {
        proposedOwner = address;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean getKycPassed(Address address) {
        return kycPassed.get(address.publicKey);
    }

    public boolean isFrozen(Address address) {
        return frozen.getOrDefault(address.publicKey, false);
    }

    public boolean isKycPassed(Address address) {
        return kycPassed.getOrDefault(address.publicKey, false);
    }

    public boolean isAllowancesEmpty() {
        return allowances.isEmpty();
    }

    public boolean isFrozenEmpty() {
        return frozen.isEmpty();
    }

    public boolean isBalanceEmpty() { return balances.isEmpty(); }

    public boolean isKycPassedEmpty() { return kycPassed.isEmpty(); }

    public boolean isPrivilegedRole(Address address) {
        return address.equals(getOwner()) ||
            address.equals(getSupplyManager()) ||
            address.equals(getAssetProtectionManager());
    }

    public void setBalance(Address address, BigInteger balance) {
        balances.put(address.publicKey, balance);
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
        this.kycPassed.put(owner.publicKey, true);
    }

    public void setKycPassed(Address address) {
        kycPassed.put(address.publicKey, true);
    }

    public void unsetKycPassed(Address address) {
        kycPassed.remove(address.publicKey);
    }

    public void freeze(Address address) {
        frozen.put(address.publicKey, true);
    }

    public void unfreeze(Address address) {
        frozen.remove(address.publicKey);
    }

    public void setAllowance(Address caller, Address spender, BigInteger value) {
        allowances.put(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey), value);
    }

    public void increaseAllowanceOf(Address allowanceOf, Address allowanceFor, BigInteger value) {
        var entry = new SimpleImmutableEntry<>(allowanceOf.publicKey, allowanceFor.publicKey);

        if (!allowances.containsKey(entry)) {
            allowances.put(entry, value);
        } else {
            allowances.merge(entry, value, BigInteger::add);
        }
    }

    public void decreaseAllowanceOf(Address allowanceOf, Address allowanceFor, BigInteger value) {
        allowances.merge(new SimpleImmutableEntry<>(allowanceOf.publicKey, allowanceFor.publicKey), value, BigInteger::subtract);
    }

    public void clearBalanceOf(Address caller) {
        balances.remove(caller.publicKey);
    }

    public void decreaseBalanceOf(Address caller, BigInteger value) {
        balances.merge(caller.publicKey, value, BigInteger::subtract);
    }

    public void decreaseTotalSupply(BigInteger value) {
        totalSupply = totalSupply.subtract(value);
    }

    public void increaseTotalSupply(BigInteger value) {
        totalSupply = totalSupply.add(value);
    }

    public void increaseBalanceOf(Address caller, BigInteger value) {
        if (!balances.containsKey(caller.publicKey)) {
            balances.put(caller.publicKey, value);
        } else {
            balances.merge(caller.publicKey, value, BigInteger::add);
        }
    }

    public boolean checkTransferAllowed(Address address) {
        return !getOwner().isZero() && !isFrozen(address) && isKycPassed(address);
    }

    public com.hedera.hashgraph.stablecoin.proto.State toProtobuf() {
        var state = com.hedera.hashgraph.stablecoin.proto.State.newBuilder()
            .setTimestamp(timestamp.getEpochSecond())
            .setTokenName(tokenName)
            .setTokenSymbol(tokenSymbol)
            .setTokenDecimal(ByteString.copyFrom(tokenDecimal.toByteArray()))
            .setTotalSupply(ByteString.copyFrom(totalSupply.toByteArray()))
            .setOwner(ByteString.copyFrom(owner.publicKey.toBytes()))
            .setSupplyManager(ByteString.copyFrom(supplyManager.publicKey.toBytes()))
            .setAssetProtectionManager(ByteString.copyFrom(assetProtectionManager.publicKey.toBytes()))
            .setProposedOwner(ByteString.copyFrom(proposedOwner.publicKey.toBytes()));

        for (var entry : balances.entrySet()) {
            state.addBalances(com.hedera.hashgraph.stablecoin.proto.Balance.newBuilder()
                .setKey(ByteString.copyFrom(entry.getKey().toBytes()))
                .setValue(ByteString.copyFrom(entry.getValue().toByteArray()))
                .build());
        }

        for (var entry : allowances.entrySet()) {
            state.addAllowances(com.hedera.hashgraph.stablecoin.proto.Allowance.newBuilder()
                .setFrom(ByteString.copyFrom(entry.getKey().getKey().toBytes()))
                .setTo(ByteString.copyFrom(entry.getKey().getValue().toBytes()))
                .setValue(ByteString.copyFrom(entry.getValue().toByteArray()))
                .build());
        }

        for (var entry : frozen.entrySet()) {
            state.addFrozen(com.hedera.hashgraph.stablecoin.proto.Frozen.newBuilder()
                .setKey(ByteString.copyFrom(entry.getKey().toBytes()))
                .setValue(entry.getValue())
                .build());
        }

        for (var entry : kycPassed.entrySet()) {
            state.addKycPassed(com.hedera.hashgraph.stablecoin.proto.KycPassed.newBuilder()
                .setKey(ByteString.copyFrom(entry.getKey().toBytes()))
                .setValue(entry.getValue())
                .build());
        }

        return state.build();
    }

    public static State fromProtobuf(com.hedera.hashgraph.stablecoin.proto.State proto) {
        var state = new State();

        state.setTimestamp(Instant.ofEpochSecond(proto.getTimestamp()));
        state.setTokenName(proto.getTokenName());
        state.setTokenSymbol(proto.getTokenSymbol());
        state.setTokenDecimal(new BigInteger(proto.getTokenDecimal().toByteArray()));
        state.setTotalSupply(new BigInteger(proto.getTotalSupply().toByteArray()));
        state.setOwner(new Address(PublicKey.fromBytes(proto.getOwner().toByteArray())));
        state.setSupplyManager(new Address(PublicKey.fromBytes(proto.getSupplyManager().toByteArray())));
        state.setAssetProtectionManager(new Address(PublicKey.fromBytes(proto.getAssetProtectionManager().toByteArray())));

        for (var balance : proto.getBalancesList()) {
            state.balances.put(
                PublicKey.fromBytes(balance.getKey().toByteArray()),
                new BigInteger(balance.getValue().toByteArray())
            );
        }

        for (var allowance : proto.getAllowancesList()) {
            state.allowances.put(
                new SimpleImmutableEntry<>(
                    PublicKey.fromBytes(allowance.getFrom().toByteArray()),
                    PublicKey.fromBytes(allowance.getTo().toByteArray())
                ),
                new BigInteger(allowance.getValue().toByteArray())
            );
        }

        for (var frozen : proto.getFrozenList()) {
            state.frozen.put(
                PublicKey.fromBytes(frozen.getKey().toByteArray()),
                frozen.getValue()
            );
        }

        for (var kycPassed : proto.getKycPassedList()) {
            state.kycPassed.put(
                PublicKey.fromBytes(kycPassed.getKey().toByteArray()),
                kycPassed.getValue()
            );
        }

        return state;
    }

    public static State tryFromFile(File file) {
        try {
            return readFromFile(file);
        } catch (IOException e) {
            return new State();
        }
    }

    public static State readFromFile(File file) throws IOException {
        var reader = new DataInputStream(new FileInputStream(file));

        return fromProtobuf(com.hedera.hashgraph.stablecoin.proto.State.parseFrom(reader));
    }

    public void writeToFile(File file) throws IOException {
        var writer = new DataOutputStream(new FileOutputStream(file));

        writer.write(toProtobuf().toByteArray());
    }
}
