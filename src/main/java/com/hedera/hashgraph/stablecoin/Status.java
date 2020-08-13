package com.hedera.hashgraph.stablecoin;

public enum Status {
    /**
     * Transaction passed all pre conditions
     */
    OK,

    CONSTRUCTOR_ASSET_PROTECTION_MANAGER_ZERO,

    /**
     * Constructor transaction called by empty address.
     */
    CONSTRUCTOR_CALLER_ZERO,

    /**
     * Constructor transaction called after contract has already been constructed.
     */
    CONSTRUCTOR_OWNER_ALREADY_SET,

    /**
     * Constrcutor transaction set `SupplyManager` to the empty address.
     */
    CONSTRUCTOR_SUPPLY_MANAGER_ZERO,

    /**
     * Contructor transaction set `TokenDecimal` to a value less than zero.
     */
    CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO,

    /**
     * Constrcutor transaction set `TotalSupply` to a value less than zero.
     */
    CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO,

    /**
     * ApproveAllowance transaction called before contract has been constrcuted.
     */
    APPROVE_ALLOWANCE_OWNER_NOT_SET,

    /**
     * ApproveAllowance transaction called with a value less than zero.
     */
    APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO,

    /**
     * ApproveAllowance transcation called by an address which is either frozen or has not passed KYC.
     */
    APPROVE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * ApproveAllowance transcation spender field is either frozen or has not passed KYC.
     */
    APPROVE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * Mint transaction called before the contract has been constructed.
     */
    MINT_OWNER_NOT_SET,

    /**
     * Mint transaction called by an unauthorized address.
     * Caller must be either `SupplyManager` or `Owner`
     */
    MINT_NOT_AUTHORIZED,

    /**
     * Mint transaction called with a value less than zero.
     */
    MINT_VALUE_LESS_THAN_ZERO,

    /**
     * Mint transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    MINT_INSUFFICENT_TOTAL_SUPPLY,

    /**
     * Burn transaction called before the contract has been constructed.
     */
    BURN_OWNER_NOT_SET,
    
    /**
     * Burn transaction called by an unautorized address.
     * Caller must be either `SupplyManager` or `Owner`.
     */
    BURN_NOT_AUTHORIZED,

    /**
     * Burn transaction called with a value less than zero.
     */
    BURN_VALUE_LESS_THAN_ZERO,

    /**
     * Burn transaction called with a value greater than `SupplyManager`'s balance.
     */
    BURN_INSUFFICENT_SUPPLY_MANAGER_BALANCE,

    /**
     * Burn transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    BURN_INSUFFICENT_TOTAL_SUPPLY,

    /**
     * Transfer transaction called before the contract has been constructed.
     */
    TRANSFER_OWNER_NOT_SET,

    /**
     * Transfer transaction called with a value less than zero.
     */
    TRANSFER_VALUE_LESS_THAN_ZERO,

    /**
     * Transfer transaction value is greater than caller's balance.
     */
    TRANSFER_INSUFFICIENT_BALANCE,

    /**
     * Transfer transaction called by an address which is either frozen or has not passed KYC. 
     */
    TRANSFER_CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * Transfer transaction to address is either frozen or has not passed KYC. 
     */
    TRANSFER_TO_ADDRESS_TRANSFER_NOT_ALLOWED,

    /**
     * TransferFrom transaction called before the contract has been constructed.
     */
    TRANSFER_FROM_OWNER_NOT_SET,

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
     * TransferFrom transaction called by an address which is either frozen or has not passed KYC. 
     */
    TRANSFER_FROM_CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * TransferFrom transaction from address is either frozen or has not passed KYC. 
     */
    TRANSFER_FROM_FROM_ADDRESS_TRANSFER_NOT_ALLOWED,

    /**
     * TransferFrom transaction to address is either frozen or has not passed KYC. 
     */
    TRANSFER_FROM_TO_ADDRESS_TRANSFER_NOT_ALLOWED,

    /**
     * ProposeOwner transaction called before the contract has been constructed.
     */
    PROPOSE_OWNER_OWNER_NOT_SET,

    /**
     * ProposeOwner transaction called by address which is not owner.
     */
    PROPOSE_OWNER_CALLER_NOT_OWNER,

    /**
     * ProposeOwner transaction with empty address.
     */
    PROPOSE_OWNER_ADDRESS_NOT_SET,

    /**
     * ProposeOwner transaction called with address which is either frozen or has not passed KYC.
     */
    PROPOSE_OWNER_TRANSFER_NOT_ALLOWED,

    /**
     * ClaimOwnership transaction called before the contract has been constructed.
     */
    CLAIM_OWNERSHIP_OWNER_NOT_SET,

    /**
     * ClaimOwnership transaction called by address which is not `ProposedOwner`.
     */
    CLAIM_OWNERSHIP_CALLER_NOT_PROPOSED_OWNER,

    /**
     * ClaimOwnership transaction called by address which is either frozen or has not passed KYC.
     */
    CLAIM_OWNERSHIP_TRANSFER_NOT_ALLOWED,

    /**
     * ChangeSupplyManager transaction called before the contract has been constructed.
     */
    CHANGE_SUPPLY_MANAGER_OWNER_NOT_SET,

    /**
     * ChangeSupplyManager transcation called by address which is not `Owner`.
     */
    CHANGE_SUPPLY_MANAGER_CALLER_NOT_OWNER,

    /**
     * ChangeSupplyManger transaction called with an empty address.
     */
    CHANGE_SUPPLY_MANAGER_ADDRESS_NOT_SET,

    /**
     * ChangeSupplyManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_SUPPLY_MANAGER_TRANSFER_NOT_ALLOWED,

    /**
     * ChangeAssetProtectionManager transaction called before the contract has been constructed.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_OWNER_NOT_SET,

    /**
     * ChangeAssetProtectionManager transcation called by address which is not `Owner`.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_CALLER_NOT_OWNER,

    /**
     * ChangeAssetProtectionManager transaction called with an empty address.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_ADDRESS_NOT_SET,

    /**
     * ChangeAssetProtectionManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_ASSET_PROTECTION_MANAGER_TRANSFER_NOT_ALLOWED,

    /**
     * Freeze transaction called before the contract has been constructed.
     */
    FREEZE_OWNER_NOT_SET,

    /**
     * Freeze transaction called by an unautorized address.
     * Caller must be either `AssetProtectionManager` or `Owner`.
     */
    FREEZE_NOT_AUTHORIZED,

    /**
     * Freeze transaction called with a privileged address.
     */
    FREEZE_ADDRESS_IS_PRIVILEGED,

    /**
     * Unfreeze transaction called before the contract has been constructed.
     */
    UNFREEZE_OWNER_NOT_SET,

    /**
     * Unfreeze transaction called by an unautorized address.
     * Caller must be either `AssetProtectionManager` or `Owner`.
     */
    UNFREEZE_NOT_AUTHORIZED,

    /**
     * Wipe transaction called before the contract has been constructed.
     */
    WIPE_OWNER_NOT_SET,

    /**
     * Wipe transaction called by an unautorized address.
     * Caller must be either `AssetProtectionManager` or `Owner`.
     */
    WIPE_NOT_AUTHORIZED,

    /**
     * Wipe transaction called with address that is not frozen.
     */
    WIPE_ADDRESS_NOT_FROZEN,

    /**
     * SetKycPassed transaction called before the contract has been constructed.
     */
    SET_KYC_PASSED_OWNER_NOT_SET,

    /**
     * SetKycPassed transaction called by an unautorized address.
     * Caller must be either `AssetProtectionManager` or `Owner`.
     */
    SET_KYC_PASSED_NOT_AUTHORIZED,

    /**
     * UnsetKycPassed transaction called before the contract has been constructed.
     */
    UNSET_KYC_PASSED_OWNER_NOT_SET,

    /**
     * UnsetKycPassed transaction called by an unautorized address.
     * Caller must be either `AssetProtectionManager` or `Owner`.
     */
    UNSET_KYC_PASSED_NOT_AUTHORIZED,

    /**
     * UnsetKycPassed transaction called with privileged address.
     */
    UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED,

    /**
     * IncreaseAllowance transaction called before the contract has been constructed.
     */
    INCREASE_ALLOWANCE_OWNER_NOT_SET,

    /**
     * IncreaseAllowance transaction called with value less than zero.
     */
    INCREASE_ALLOWANCE_VALUE_IS_ZERO,

    /**
     * IncreaseAllowance called by address which is either frozen or has not passed KYC.
     */
    INCREASE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * IncreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * DecreaseAllowance transaction called before the contract has been constructed.
     */
    DECREASE_ALLOWANCE_OWNER_NOT_SET,
    
    /**
     * DecreaseAllowance transaction called with value less than zero.
     */
    DECREASE_ALLOWANCE_VALUE_IS_ZERO,

    /**
     * DecreaseAllowance transaction called by address which is either frozen or has not passed KYC.
     */
    DECREASE_ALLOWANCE_CALLER_TRANSFER_NOT_ALLOWED,

    /**
     * DecreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    DECREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED,

    /**
     * DecreaseAllowance transation called with value that exceeds caller to spender allowance.
     */
    DECREASE_ALLOWANCE_VALUE_EXCEEDS_ALLOWANCE
}
