package com.hedera.hashgraph.stablecoin;

public enum Status {
    OK,

    /**
     * Caller of a transaction is the zero address.
     */
    CALLER_ZERO,

    /**
     * Owner of the token is the zero address.
     * This status code will be raised for transactions that are non-construct
     * before the construct transaction.
     */
    OWNER_ZERO,

    CONSTRUCTOR_OWNER_ALREADY_SET,
    CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO,
    CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO,
    CONSTRUCTOR_SUPPLY_MANAGER_ZERO,
    CONSTRUCTOR_ASSET_PROTECTION_MANAGER_ZERO,

    TRANSFER_VALUE_LESS_THAN_ZERO,
    TRANSFER_INSUFFICIENT_BALANCE,
    TRANSFER_NOT_ALLOWED,

    APPROVE_OWNER_NOT_SET,
    APPROVE_VALUE_LESS_THAN_ZERO,
    APPROVE_CALLER_TRANSFER_NOT_ALLOWED,
    APPROVE_SPENDER_TRANSFER_NOT_ALLOWED
}
