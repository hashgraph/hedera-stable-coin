package com.hedera.hashgraph.stablecoin.app.repository;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public enum TransactionKind {
    CONSTRUCT(1),
    APPROVE_ALLOWANCE(2),
    MINT(3),
    BURN(4),
    TRANSFER(5),
    TRANSFER_FROM(6),
    PROPOSE_OWNER(7),
    CLAIM_OWNERSHIP(8),
    CHANGE_SUPPLY_MANAGER(9),
    CHANGE_COMPLIANCE_MANAGER(10),
    FREEZE(11),
    UNFREEZE(12),
    WIPE(13),
    SET_KYC_PASSED(14),
    UNSET_KYC_PASSED(15),
    INCREASE_ALLOWANCE(16),
    DECREASE_ALLOWANCE(17),
    CHANGE_ENFORCEMENT_MANAGER(18),
    APPROVE_EXTERNAL_TRANSFER(19),
    EXTERNAL_TRANSFER(20),
    EXTERNAL_TRANSFER_FROM(21);

    private static final Map<Integer, TransactionKind> possibleValues = Arrays.stream(values())
        .collect(Collectors.toMap(TransactionKind::getValue, identity()));

    private final int value;

    private TransactionKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @SuppressWarnings("NullAway")
    public TransactionKind valueOf(int value) {
        return possibleValues.get(value);
    }
}
