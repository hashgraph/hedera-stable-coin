package com.hedera.hashgraph.stablecoin;

import com.hedera.hashgraph.sdk.PublicKey;

import java.math.BigInteger;
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

    State() {
    }

    public Address getOwner() {
        return owner;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
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
}
