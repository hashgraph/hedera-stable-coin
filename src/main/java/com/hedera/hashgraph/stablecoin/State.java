package com.hedera.hashgraph.stablecoin;

import com.hedera.hashgraph.sdk.PublicKey;

import java.math.BigInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class State {
    /**
     * Display name of the stable coin (ex., Hbar, Ether).
     */
    private String tokenName;

    /**
     * Ticker symbol for the stable coin (ex., ETH, USD, etc.).
     */
    private String tokenSymbol;

    /**
     * The owner of the stable coin instance. The owner has control over all administrative controls and
     * can re-assign the asset protection manager and supply manager.
     */
    private Address owner;

    private Address supplyManager;

    private Address assetProtectionManager;

    /**
     * The proposed owner may be set at any time by the current owner. The proposed owner can then claim their
     * ownership which will change the owner property.
     */
    private Address proposedOwner;

    private BigInteger totalSupply;

    private ConcurrentMap<PublicKey, BigInteger> balances = new ConcurrentHashMap<>();

    private ConcurrentMap<PublicKey, Boolean> frozen = new ConcurrentHashMap<>();

    private ConcurrentMap<PublicKey, Boolean> kycPassed = new ConcurrentHashMap<>();

    private ConcurrentMap<SimpleImmutableEntry<PublicKey, PublicKey>, BigInteger> allowances = new ConcurrentHashMap<>();

    State() {
    }

    public boolean hasOwner() {
        return owner != null && !owner.isZero();
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getTokenSymbol() { return tokenSymbol; }

    public Address getSupplyManager() {
        return supplyManager;
    }

    public BigInteger getBalanceOf(Address address) {
        return balances.getOrDefault(address.publicKey, BigInteger.ZERO);
    }

    public boolean isFrozen(Address address) {
        return frozen.getOrDefault(address.publicKey, false);
    }

    public boolean isKycPassed(Address address) {
        return kycPassed.getOrDefault(address.publicKey, false);
    }

    public void setOwner(Address owner) {
        this.owner = owner;
        this.kycPassed.put(owner.publicKey, true);
    }

    public void setSupplyManager(Address supplyManager) {
        this.supplyManager = supplyManager;
    }

    /**
     * Set the token display name.
     * May only be called once (during construct).
     */
    public void setTokenName(String tokenName) {
        // this should not be being called more than once
        assert this.tokenName.isEmpty();
        assert !tokenName.isEmpty();

        this.tokenName = tokenName;
    }

    /**
     * Set the token ticker symbol.
     * May only be called once (during construct).
     */
    public void setTokenSymbol(String tokenSymbol) {
        // this should not be being called more than once
        assert this.tokenSymbol.isEmpty();
        assert !tokenSymbol.isEmpty();

        this.tokenSymbol = tokenSymbol;
    }

    public void decreaseBalanceOf(Address caller, BigInteger value) {
        // no need for putIfAbsent, this should not be called unless there
        // is value there
        balances.merge(caller.publicKey, value, BigInteger::subtract);
    }

    public void increaseBalanceOf(Address caller, BigInteger value) {
        if (balances.containsKey(caller.publicKey)) {
            balances.put(caller.publicKey, value);
        } else {
            balances.merge(caller.publicKey, value, BigInteger::add);
        }
    }
}
