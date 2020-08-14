package com.hedera.hashgraph.stablecoin;

public enum Status {
    OK,

    /**
     * Any transaction except for the constructor called when the owner is not set.
     */
    OWNER_NOT_SET,

    /** Any transaction called with an invalid signature. */
    INVALID_SIGNATURE,

    /**
     * Any transaction called with an empty caller.
     */
    CALLER_NOT_SET,

    /** Any transaction called with a caller that is not authorized to call that transaction. */
    CALLER_NOT_AUTHORIZED,

    /**
     * Any transaction called with a caller that is either frozen or has not passed KYC (where that is required).
     */
    CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * Constructor called with an empty value for asset protection manager.
     */
    CONSTRUCTOR_ASSET_PROTECTION_MANAGER_NOT_SET,

    /**
     * Constructor called after contract has already been constructed.
     */
    CONSTRUCTOR_OWNER_ALREADY_SET,

    /**
     * Constructor called with an empty value for supply manager.
     */
    CONSTRUCTOR_SUPPLY_MANAGER_NOT_SET,

    /**
     * Constructor called with a value less than zero for token decimal.
     */
    CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO,

    /**
     * Constructor called with a value less than zero for total supply.
     */
    CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO,

    /**
     * Approve allowance called with a value less than zero.
     */
    APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO,

    /**
     * Approve allowance called with a spender that is either frozen or has not passed KYC.
     */
    APPROVE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * Mint transaction called with a value less than zero.
     */
    MINT_VALUE_LESS_THAN_ZERO,

    /**
     * Mint transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    MINT_INSUFFICIENT_TOTAL_SUPPLY,

    /**
     * Burn transaction called with a value less than zero.
     */
    BURN_VALUE_LESS_THAN_ZERO,

    /**
     * Burn transaction called with a value greater than `SupplyManager`'s balance.
     */
    BURN_INSUFFICIENT_SUPPLY_MANAGER_BALANCE,

    /**
     * Burn transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    BURN_INSUFFICIENT_TOTAL_SUPPLY,

    /**
     * Transfer transaction called with a value less than zero.
     */
    TRANSFER_VALUE_LESS_THAN_ZERO,

    /**
     * Transfer transaction value is greater than caller's balance.
     */
    TRANSFER_INSUFFICIENT_BALANCE,

    /**
     * Transfer transaction to address is either frozen or has not passed KYC.
     */
    TRANSFER_TO_TRANSFER_NOT_ALLOWED,

    /**
     * TransferFrom transaction called with a value less than zero.
     */
    TRANSFER_FROM_VALUE_LESS_THAN_ZERO,

    /**
     * TransferFrom transaction value exceeds caller's balance.
     */
    TRANSFER_FROM_INSUFFICIENT_BALANCE,

    /**
     * TransferFrom transaction value exceeds `From` to `Caller` allowance.
     */
    TRANSFER_FROM_INSUFFICIENT_ALLOWANCE,

    /**
     * TransferFrom transaction from address is either frozen or has not passed KYC.
     */
    TRANSFER_FROM_FROM_TRANSFER_NOT_ALLOWED,

    /**
     * TransferFrom transaction to address is either frozen or has not passed KYC.
     */
    TRANSFER_FROM_TO_TRANSFER_NOT_ALLOWED,

    /**
     * ProposeOwner transaction with empty address.
     */
    PROPOSE_OWNER_ADDRESS_NOT_SET,

    /**
     * ProposeOwner transaction called with address which is either frozen or has not passed KYC.
     */
    PROPOSE_OWNER_TRANSFER_NOT_ALLOWED,

    /**
     * ClaimOwnership transaction called with address which is either frozen or has not passed KYC.
     */
    CLAIM_OWNERSHIP_TRANSFER_NOT_ALLOWED,

    /**
     * ChangeSupplyManger transaction called with an empty address.
     */
    CHANGE_SUPPLY_MANAGER_ADDRESS_NOT_SET,

    /**
     * ChangeSupplyManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_SUPPLY_MANAGER_TRANSFER_NOT_ALLOWED,

    /**
     * ChangeAssetProtectionManager transaction called with an empty address.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_ADDRESS_NOT_SET,

    /**
     * ChangeAssetProtectionManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_TRANSFER_NOT_ALLOWED,

    /**
     * Freeze transaction called with a privileged address.
     */
    FREEZE_ADDRESS_IS_PRIVILEGED,

    /**
     * Wipe transaction called with address that is not frozen.
     */
    WIPE_ADDRESS_NOT_FROZEN,

    /**
     * UnsetKycPassed transaction called with privileged address.
     */
    UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED,

    /**
     * IncreaseAllowance transaction called with value less than zero.
     */
    INCREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO,

    /**
     * IncreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * DecreaseAllowance transaction called with value less than zero.
     */
    DECREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO,

    /**
     * DecreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    DECREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * DecreaseAllowance transaction called with value that exceeds caller to spender allowance.
     */
    DECREASE_ALLOWANCE_VALUE_EXCEEDS_ALLOWANCE
}
