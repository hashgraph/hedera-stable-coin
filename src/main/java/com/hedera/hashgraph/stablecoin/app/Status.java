package com.hedera.hashgraph.stablecoin.app;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public enum Status {
    OK(0),

    /**
     * Any transaction except for the constructor called when the owner is not set.
     */
    OWNER_NOT_SET(1),

    /**
     * Any transaction called with an invalid signature.
     */
    INVALID_SIGNATURE(2),

    /**
     * Any transaction called with an empty caller.
     */
    CALLER_NOT_SET(3),

    /**
     * Any transaction called with a caller that is not authorized to call that transaction.
     */
    CALLER_NOT_AUTHORIZED(4),

    /**
     * Any transaction called with an unexpected nonce.
     */
    UNEXPECTED_NONCE(5),

    /**
     * Any transaction called with a caller that is either frozen or has not passed KYC (where that is required).
     */
    CALLER_TRANSFER_NOT_ALLOWED(6),

    /**
     * Constructor called with an empty value for compliance manager.
     */
    CONSTRUCTOR_COMPLIANCE_MANAGER_NOT_SET(7),

    /**
     * Constructor called after contract has already been constructed.
     */
    CONSTRUCTOR_OWNER_ALREADY_SET(8),

    /**
     * Constructor called with an empty value for supply manager.
     */
    CONSTRUCTOR_SUPPLY_MANAGER_NOT_SET(9),

    /**
     * Constructor called with a value less than zero for token decimal.
     */
    CONSTRUCTOR_TOKEN_DECIMAL_LESS_THAN_ZERO(10),

    /**
     * Constructor called with a value less than zero for total supply.
     */
    CONSTRUCTOR_TOTAL_SUPPLY_LESS_THAN_ZERO(11),

    /**
     * Approve allowance called with a value less than zero.
     */
    APPROVE_ALLOWANCE_VALUE_LESS_THAN_ZERO(12),

    /**
     * Approve allowance called with a spender that is either frozen or has not passed KYC.
     */
    APPROVE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED(13),

    /**
     * Mint transaction called with a value less than zero.
     */
    MINT_VALUE_LESS_THAN_ZERO(14),

    /**
     * Mint transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    MINT_INSUFFICIENT_TOTAL_SUPPLY(15),

    /**
     * Burn transaction called with a value less than zero.
     */
    BURN_VALUE_LESS_THAN_ZERO(16),

    /**
     * Burn transaction called with a value greater than `SupplyManager`'s balance.
     */
    BURN_INSUFFICIENT_SUPPLY_MANAGER_BALANCE(17),

    /**
     * Burn transaction called when `TotalSupply` exceeds `SupplyManager`'s balance.
     */
    BURN_INSUFFICIENT_TOTAL_SUPPLY(18),

    /**
     * Transfer transaction called with a value less than zero.
     */
    TRANSFER_VALUE_LESS_THAN_ZERO(19),

    /**
     * Transfer transaction value is greater than caller's balance.
     */
    TRANSFER_INSUFFICIENT_BALANCE(20),

    /**
     * Transfer transaction to address is either frozen or has not passed KYC.
     */
    TRANSFER_TO_TRANSFER_NOT_ALLOWED(21),

    /**
     * TransferFrom transaction called with a value less than zero.
     */
    TRANSFER_FROM_VALUE_LESS_THAN_ZERO(22),

    /**
     * TransferFrom transaction value exceeds caller's balance.
     */
    TRANSFER_FROM_INSUFFICIENT_BALANCE(23),

    /**
     * TransferFrom transaction value exceeds `From` to `Caller` allowance.
     */
    TRANSFER_FROM_INSUFFICIENT_ALLOWANCE(24),

    /**
     * TransferFrom transaction from address is either frozen or has not passed KYC.
     */
    TRANSFER_FROM_FROM_TRANSFER_NOT_ALLOWED(25),

    /**
     * TransferFrom transaction to address is either frozen or has not passed KYC.
     */
    TRANSFER_FROM_TO_TRANSFER_NOT_ALLOWED(26),

    /**
     * ProposeOwner transaction with empty address.
     */
    PROPOSE_OWNER_ADDRESS_NOT_SET(27),

    /**
     * ProposeOwner transaction called with address which is either frozen or has not passed KYC.
     */
    PROPOSE_OWNER_TRANSFER_NOT_ALLOWED(28),

    /**
     * ClaimOwnership transaction called with address which is either frozen or has not passed KYC.
     */
    CLAIM_OWNERSHIP_TRANSFER_NOT_ALLOWED(29),

    /**
     * ChangeSupplyManger transaction called with an empty address.
     */
    CHANGE_SUPPLY_MANAGER_ADDRESS_NOT_SET(30),

    /**
     * ChangeSupplyManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_SUPPLY_MANAGER_TRANSFER_NOT_ALLOWED(31),

    /**
     * ChangeComplianceManager transaction called with an empty address.
     */
    CHANGE_COMPLIANCE_MANAGER_ADDRESS_NOT_SET(32),

    /**
     * ChangeComplianceManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_COMPLIANCE_MANAGER_TRANSFER_NOT_ALLOWED(33),

    /**
     * Freeze transaction called with a privileged address.
     */
    FREEZE_ADDRESS_IS_PRIVILEGED(34),

    /**
     * Wipe value is larger than the balance of the address, this wipe would result in a negative balance.
     */
    WIPE_VALUE_WOULD_RESULT_IN_NEGATIVE_BALANCE(35),

    /**
     * UnsetKycPassed transaction called with privileged address.
     */
    UNSET_KYC_PASSED_ADDRESS_IS_PRIVILEGED(36),

    /**
     * IncreaseAllowance transaction called with value less than zero.
     */
    INCREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO(37),

    /**
     * IncreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    INCREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED(38),

    /**
     * DecreaseAllowance transaction called with value less than zero.
     */
    DECREASE_ALLOWANCE_VALUE_LESS_THAN_ZERO(39),

    /**
     * DecreaseAllowance transaction spender is either frozen or has not passed KYC.
     */
    DECREASE_ALLOWANCE_SPENDER_TRANSFER_NOT_ALLOWED(40),

    /**
     * DecreaseAllowance transaction called with value that exceeds caller to spender allowance.
     */
    DECREASE_ALLOWANCE_VALUE_EXCEEDS_ALLOWANCE(41),

    /**
     * Any Transaction called with value or resulting value above the max value for a uint256 number.
     */
    NUMBER_VALUES_LIMITED_TO_256_BITS(42),

    /**
     * Constructor called with an empty value for enforcement manager.
     */
    CONSTRUCTOR_ENFORCEMENT_MANAGER_NOT_SET(43),

    /**
     * ChangeEnforcementManager transaction called with an empty address.
     */
    CHANGE_ENFORCEMENT_MANAGER_ADDRESS_NOT_SET(44),

    /**
     * ChangeEnforcementManager transaction called with address that either is frozen or has not passed KYC.
     */
    CHANGE_ENFORCEMENT_MANAGER_TRANSFER_NOT_ALLOWED(45),

    EXTERNAL_TRANSFER_NOT_ALLOWED(46),

    ;

    private static final Map<Integer, Status> possibleValues = Arrays.stream(values())
        .collect(Collectors.toMap(Status::getValue, identity()));

    private final int value;

    private Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @SuppressWarnings("NullAway")
    public static Status valueOf(int value) {
        return possibleValues.get(value);
    }
}
