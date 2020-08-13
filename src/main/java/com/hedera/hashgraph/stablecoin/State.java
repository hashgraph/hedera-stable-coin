package com.hedera.hashgraph.stablecoin;

import com.hedera.hashgraph.sdk.PublicKey;

import java.math.BigInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;

public final class State {
    /**
     * Display name of the stable coin (ex., Hbar, Ether).
     */
    private String tokenName;

    /**
     * Ticker symbol for the stable coin (ex., ETH, USD, etc.).
     */
    private String tokenSymbol;

    private BigInteger tokenDecimal;

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

    private Map<PublicKey, BigInteger> balances = new HashMap<>();

    private Map<PublicKey, Boolean> frozen = new HashMap<>();

    private Map<PublicKey, Boolean> kycPassed = new HashMap<>();

    private Map<SimpleImmutableEntry<PublicKey, PublicKey>, BigInteger> allowances = new HashMap<>();

    State() {
    }

    public boolean hasOwner() {
        return owner != null && !owner.isZero();
    }

    public Address getOwner() {
        return owner;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getTokenSymbol() { return tokenSymbol; }

    public BigInteger getTokenDecimal() {
        return tokenDecimal;
    }

    public BigInteger getTotalSupply() {
        return totalSupply;
    }

    public Address getSupplyManager() {
        return supplyManager;
    }

    public Address getAssetProtectionManager() {
        return assetProtectionManager;
    }

    public BigInteger getAllowance(Address caller, Address spender) {
        return allowances.get(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey));
    }

    public BigInteger getBalanceOf(Address address) {
        return balances.getOrDefault(address.publicKey, BigInteger.ZERO);
    }

    public Address getProposedOwner() {
        return proposedOwner;
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

    public void setTokenDecimal(BigInteger tokenDecimal) {
        // this should not be being called more than once
        assert this.tokenDecimal == null;

        this.tokenDecimal = tokenDecimal;
    }

    public void setTotalSupply(BigInteger totalSupply) {
        this.totalSupply = totalSupply;
    }

    public void setAssetProtectionManager(Address assetProtectionManager) {
        this.assetProtectionManager = assetProtectionManager;
    }

    public void setKycPassed(Address address, boolean passed) {
        kycPassed.put(address.publicKey, passed);
    }

    public void setAllowance(Address caller, Address spender, BigInteger value) {
        allowances.put(new SimpleImmutableEntry<>(caller.publicKey, spender.publicKey), value);
    }

    public void decreaseBalanceOf(Address caller, BigInteger value) {
        // no need for putIfAbsent, this should not be called unless there
        // is value there
        balances.merge(caller.publicKey, value, BigInteger::subtract);
    }

    public void increaseBalanceOf(Address caller, BigInteger value) {
        if (!balances.containsKey(caller.publicKey)) {
            balances.put(caller.publicKey, value);
        } else {
            balances.merge(caller.publicKey, value, BigInteger::add);
        }
    }

    public boolean checkTransferAllowed(Address address) {
        return hasOwner() && !isFrozen(address) && isKycPassed(address);
    }

}
