CREATE TABLE address_event
(
    address         BYTEA NOT NULL,

    event_timestamp INT8  NOT NULL,
    event_index     INT2  NOT NULL,

    PRIMARY KEY (address, event_timestamp, event_index)
);

CREATE TABLE event
(
    -- nanoseconds since the epoch
    timestamp INT8 NOT NULL,
    index     INT2 NOT NULL,

    PRIMARY KEY (timestamp, index),

    -- kind of event
    --  1 = Construct
    --  2 = Approve
    --  3 = Mint
    --  4 = Burn
    --  5 = Transfer
    --  6 = ProposeOwner
    --  7 = ClaimOwner
    --  8 = ChangeAssetProtectionManager
    --  9 = ChangeSupplyManager
    -- 10 = Freeze
    -- 11 = Unfreeze
    -- 12 = Wipe
    -- 13 = SetKycPassed
    -- 14 = UnsetKycPassed
    -- 15 = IncreaseAllowance
    -- 16 = DecreaseAllowance
    kind      INT2 NOT NULL
);

-- Constructed(tokenName, tokenSymbol, tokenDecimal, totalSupply,
--             supplyManager, assetProtectionManager)
CREATE TABLE event_construct
(
    timestamp                INT8           NOT NULL,
    index                    INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    token_name               TEXT           NOT NULL,
    token_symbol             TEXT           NOT NULL,
    token_decimal            NUMERIC(78, 0) NOT NULL,
    total_supply             NUMERIC(78, 0) NOT NULL,
    supply_manager           BYTEA          NOT NULL,
    asset_protection_manager BYTEA          NOT NULL
);

-- Approve(caller, spender, value)
CREATE TABLE event_approve
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA          NOT NULL,
    spender   BYTEA          NOT NULL,
    value     NUMERIC(78, 0) NOT NULL
);

-- Mint(SupplyManager, value)
CREATE TABLE event_mint
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    value     NUMERIC(78, 0) NOT NULL
);

-- Burn(SupplyManager, value)
CREATE TABLE event_burn
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    value     NUMERIC(78, 0) NOT NULL
);

-- Transfer(caller, to, value)
CREATE TABLE event_transfer
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    sender    BYTEA          NOT NULL,
    receiver  BYTEA          NOT NULL,
    value     NUMERIC(78, 0) NOT NULL
);

-- ProposeOwner(addr)
CREATE TABLE event_propose_owner
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- ClaimOwnership(addr)
CREATE TABLE event_claim_owner
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- ChangeAssetProtectionManager(addr)
CREATE TABLE event_change_asset_protection_manager
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- ChangeSupplyManager(addr)
CREATE TABLE event_change_supply_manager
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- Freeze(addr)
CREATE TABLE event_freeze
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- Unfreeze(addr)
CREATE TABLE event_unfreeze
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- Wipe(addr)
CREATE TABLE event_wipe
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA          NOT NULL,
    amount    NUMERIC(78, 0) NOT NULL
);

-- SetKycPassed(addr)
CREATE TABLE event_set_kyc_passed
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- UnsetKycPassed(addr)
CREATE TABLE event_unset_kyc_passed
(
    timestamp INT8  NOT NULL,
    index     INT2  NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA NOT NULL
);

-- IncreaseAllowance(caller, spender, amount)
CREATE TABLE event_increase_allowance
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA          NOT NULL,
    spender   BYTEA          NOT NULL,
    allowance NUMERIC(78, 0) NOT NULL
);

-- DecreaseAllowance(caller, spender, amount)
CREATE TABLE event_decrease_allowance
(
    timestamp INT8           NOT NULL,
    index     INT2           NOT NULL,

    PRIMARY KEY (timestamp, index),

    address   BYTEA          NOT NULL,
    spender   BYTEA          NOT NULL,
    allowance NUMERIC(78, 0) NOT NULL
);
