CREATE TABLE address_transaction
(
    "address"               BYTEA NOT NULL,
    "transaction_timestamp" INT8  NOT NULL,

    PRIMARY KEY (address, transaction_timestamp)
);

CREATE TABLE "transaction"
(
    -- nanoseconds since the epoch
    "timestamp" INT8 PRIMARY KEY,

    -- pass/fail of the transaction
    -- values come from <Status.java> in src/
    "status"    INT2  NOT NULL,

    -- kind of transaction
    -- values come from the proto field IDs in <TransactionBody.proto>
    "kind"      INT2  NOT NULL,

    -- address of the caller
    "caller"    BYTEA NOT NULL,

    -- valid start time for the transaction
    -- nanos since epoch
    "valid_start"  INT8 NOT NULL
);

CREATE INDEX ON "transaction" ( "caller", "valid_start" );
CREATE INDEX ON "transaction" ( "status" );
CREATE INDEX ON "transaction" ( "kind" );

-- void constructor(String tokenName, String tokenSymbol, Int
--      tokenDecimal, Int totalSupply, Address supplyManager, Address
--      complianceManager)
CREATE TABLE transaction_construct
(
    "timestamp"         INT8 PRIMARY KEY,

    token_name          TEXT           NOT NULL,
    token_symbol        TEXT           NOT NULL,
    token_decimal       NUMERIC(78, 0) NOT NULL,
    total_supply        NUMERIC(78, 0) NOT NULL,
    supply_manager      BYTEA          NOT NULL,
    compliance_manager  BYTEA          NOT NULL,
    enforcement_manager BYTEA          NOT NULL
);

-- void approveAllowance(Address spender, Int value)
CREATE TABLE transaction_approve_allowance
(
    "timestamp" INT8 PRIMARY KEY,

    spender     BYTEA          NOT NULL,
    value       NUMERIC(78, 0) NOT NULL
);

-- void mint(Int value)
CREATE TABLE transaction_mint
(
    "timestamp" INT8 PRIMARY KEY,

    value       NUMERIC(78, 0) NOT NULL
);

-- void burn(Int value)
CREATE TABLE transaction_burn
(
    "timestamp" INT8 PRIMARY KEY,

    value       NUMERIC(78, 0) NOT NULL
);

-- void transfer(Address to, Int value)
CREATE TABLE transaction_transfer
(
    "timestamp" INT8 PRIMARY KEY,

    receiver    BYTEA          NOT NULL,
    value       NUMERIC(78, 0) NOT NULL
);

-- void transferFrom(Address from, Address to, Int value)
CREATE TABLE transaction_transfer_from
(
    "timestamp" INT8 PRIMARY KEY,

    sender      BYTEA          NOT NULL,
    receiver    BYTEA          NOT NULL,
    value       NUMERIC(78, 0) NOT NULL
);

-- void proposeOwner(Address addr)
CREATE TABLE transaction_propose_owner
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void claimOwnership()
-- NOTE: no need for an arguments table

-- void changeSupplyManager(Address addr)
CREATE TABLE transaction_change_supply_manager
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void changeComplianceManager(Address addr)
CREATE TABLE transaction_change_compliance_manager
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void changeEnforcementManager(Address addr)
CREATE TABLE transaction_change_enforcement_manager
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void freeze(Address addr)
CREATE TABLE transaction_freeze
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void unfreeze(Address addr)
CREATE TABLE transaction_unfreeze
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void wipe(Address addr)
CREATE TABLE transaction_wipe
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA          NOT NULL,

    value       NUMERIC(78, 0) NOT NULL
);

-- void setKycPassed(Address addr)
CREATE TABLE transaction_set_kyc_passed
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void unsetKycPassed(Address addr)
CREATE TABLE transaction_unset_kyc_passed
(
    "timestamp" INT8 PRIMARY KEY,

    address     BYTEA NOT NULL
);

-- void increaseAllowance(Address spender, Int value)
CREATE TABLE transaction_increase_allowance
(
    "timestamp" INT8 PRIMARY KEY,

    spender     BYTEA          NOT NULL,
    value       NUMERIC(78, 0) NOT NULL
);

-- void decreaseAllowance(Address spender, Int value)
CREATE TABLE transaction_decrease_allowance
(
    "timestamp" INT8 PRIMARY KEY,

    spender     BYTEA          NOT NULL,
    value       NUMERIC(78, 0) NOT NULL
);

-- void approveExternalTransfer(String networkURI, Bytes to, Int value)
CREATE TABLE transaction_approve_external_transfer
(
    "timestamp" INT8 PRIMARY KEY,

    network_uri TEXT           NOT NULL,
    address_to  BYTEA          NOT NULL,
    amount      NUMERIC(78, 0) NOT NULL
);

-- void externalTransfer(Address from, String networkURI, Bytes to, Int value)
CREATE TABLE transaction_external_transfer
(
    "timestamp"  INT8 PRIMARY KEY,

    address_from BYTEA          NOT NULL,
    network_uri  TEXT           NOT NULL,
    address_to   BYTEA          NOT NULL,
    amount       NUMERIC(78, 0) NOT NULL
);

-- void externalTransferFrom(Bytes from, String networkURI, Address to, Int value)
CREATE TABLE transaction_external_transfer_from
(
    "timestamp"  INT8 PRIMARY KEY,

    address_from BYTEA          NOT NULL,
    network_uri  TEXT           NOT NULL,
    address_to   BYTEA          NOT NULL,
    amount       NUMERIC(78, 0) NOT NULL
);
