package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PublicKey;
import com.hedera.hashgraph.stablecoin.sdk.Address;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class State {
    final Map<Ed25519PublicKey, BigInteger> balances = new HashMap<>();

    final Set<Ed25519PublicKey> frozen = new HashSet<>();

    final Set<Ed25519PublicKey> kycPassed = new HashSet<>();

    final Map<SimpleImmutableEntry<Ed25519PublicKey, Ed25519PublicKey>, BigInteger> allowances = new HashMap<>();

    final Map<Tuple3, BigInteger> externalAllowances = new HashMap<>();

    final Map<TransactionId, TransactionReceipt> transactionReceipts = new TreeMap<>(Comparator.comparing(transactionId -> transactionId.validStart));

    /**
     * Used to lock write access to state during state snapshot
     * serialization.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Display name of the stable coin (ex., Hbar, Ether).
     */
    private String tokenName = "";

    /**
     * Ticker symbol for the stable coin (ex., ETH, USD, etc.).
     */
    private String tokenSymbol = "";

    private int tokenDecimal = 0;

    /**
     * The owner of the stable coin instance. The owner has control over all administrative controls and
     * can re-assign the asset protection manager and supply manager.
     */
    private Address owner = Address.ZERO;

    private Address supplyManager = Address.ZERO;

    private Address complianceManager = Address.ZERO;

    private Address enforcementManager = Address.ZERO;

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

    public void setOwner(Address owner) {
        this.owner = owner;
        this.kycPassed.add(owner.publicKey);
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

    public int getTokenDecimal() {
        return tokenDecimal;
    }

    public void setTokenDecimal(int tokenDecimal) {
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

    public Address getComplianceManager() {
        return complianceManager;
    }

    public void setComplianceManager(Address complianceManager) {
        this.complianceManager = complianceManager;
    }

    public Address getEnforcementManager() {
        return enforcementManager;
    }

    public void setEnforcementManager(Address enforcementManager) {
        this.enforcementManager = enforcementManager;
    }

    public BigInteger getAllowance(Address caller, Address spender) {
        return allowances.getOrDefault(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey), BigInteger.ZERO);
    }

    public BigInteger getExternalAllowance(Address from, String networkURI, byte[] to) {
        return externalAllowances.getOrDefault(new Tuple3(from.publicKey, networkURI, to), BigInteger.ZERO);
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

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isFrozen(Address address) {
        return frozen.contains(address.publicKey);
    }

    public boolean isKycPassed(Address address) {
        return kycPassed.contains(address.publicKey);
    }

    public boolean isPrivilegedRole(Address address) {
        return address.equals(getOwner()) ||
            address.equals(getComplianceManager()) ||
            address.equals(getSupplyManager()) ||
            address.equals(getEnforcementManager());
    }

    public void setKycPassed(Address address) {
        kycPassed.add(address.publicKey);
    }

    public void unsetKycPassed(Address address) {
        kycPassed.remove(address.publicKey);
    }

    public void freeze(Address address) {
        frozen.add(address.publicKey);
    }

    public void unfreeze(Address address) {
        frozen.remove(address.publicKey);
    }

    public void setAllowance(Address caller, Address spender, BigInteger value) {
        allowances.put(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey), value);
    }

    public void setExternalAllowance(Address caller, String networkURI, byte[] to, BigInteger amount) {
        externalAllowances.put(new Tuple3(caller.publicKey, networkURI, to), amount);
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

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    /**
     * Adds the transaction receipt.
     * Returns `false` if the ID already existed.
     */
    public void addTransactionReceipt(TransactionId transactionId, TransactionReceipt transactionReceipt) {
        transactionReceipts.putIfAbsent(transactionId, transactionReceipt);
    }

    /**
     * Get a transaction receipt by ID.
     */
    @Nullable
    public TransactionReceipt getTransactionReceipt(TransactionId transactionId) {
        return transactionReceipts.get(transactionId);
    }

    /**
     * Returns `false` if there is an existing ID.
     */
    public boolean hasTransactionId(TransactionId transactionId) {
        return transactionReceipts.containsKey(transactionId);
    }

    /**
     * Clean up memory by removing expired transaction IDs. No need to keep
     * them in memory to prevent duplicates after the ID expires.
     */
    void removeExpiredTransactionReceipts() {
        var now = Instant.now();
        var receipts = transactionReceipts.entrySet().iterator();

        while (receipts.hasNext()) {
            var receipt = receipts.next();

            if (!receipt.getValue().isExpired(now)) {
                break;
            }

            receipts.remove();
        }
    }
}
