# Hedera Stable Coin

## Prerequisites

 - Java 14+

 - PostgreSQL 12+

 - [TimescaleDB](https://www.timescale.com/)

## Build

Create the development database. Assumes that TimescaleDB is running on `localhost:5432`

```
createdb -h localhost -U postgres stable_coin
```

Run database migrations.

```
./gradlew flywayMigrate
```

Assemble JAR.

```
./gradlew jooqGenerate build
```

## Prepare environment variables

```
cp .env.sample .env
nano .env
```

set the following environment variables

### Required variables

These variables must be set to your environment before starting the token node

#### Database information for transaction and event logging

- HSC_DATABASE_URL=postgresql://localhost:5432/stable_coin
- HSC_DATABASE_USERNAME=postgres
- HSC_DATABASE_PASSWORD=password

#### Operator on Hedera to use to execute any transactions (This is the account that will get charged).

Note that an operator _is not required_ to run a token node on an existing topic.

- HSC_OPERATOR_ID={0.0.___}
- HSC_OPERATOR_KEY={Private key 302e___}

### After first run

After the first token node is run, it will output a `topic id` for subsequent uses, copy this `topic id` and update below.

_Leave this unset to create a new contract on a new topic. Requires an operator to be set._

HSC_TOPIC_ID={0.0.____}

### Optional variables

These variables are defaulted and may be ignored for initial testing

#### Required when creating a new stable coin contract instance.

- HSC_TOKEN_NAME=Fire
- HSC_TOKEN_SYMBOL=XFR
- HSC_TOKEN_DECIMAL=2
- HSC_TOTAL_SUPPLY=10000

#### Hedera Network to use (mainnet, testnet, previewnet)

- HSC_NETWORK=testnet

#### Optional when creating a new stable coin contract instance.

Defaults to the operator key.

_Note: Running `java -jar build/libs/stable-coin-0.2.0.jar --newkeys` will generate and output a set of keys you may use here in the environment file._

**Remember to `uncomment` these variables.**

- HSC_OWNER_KEY={Private Key 302___}
- HSC_SUPPLY_MANAGER_KEY={Public Key 302___}
- HSC_COMPLIANCE_MANAGER_KEY={Public Key 302___}
- HSC_ENFORCEMENT_MANAGER_KEY={Public Key 302___}

#### Port the HTTP service will be served on

- HSC_STATE_PORT=9000

#### How often (in seconds) to commit to a persistent store for logging and state.

- HSC_COMMIT_INTERVAL=5

#### Directory to store state snapshots.

The token node will read the latest snapshot on launch to skip reading from the entire topic.

- HSC_STATE_DIR=_state

#### Maximum number of state snaphots to keep in the state directory

- HSC_STATE_HISTORY_SIZE=3

#### Optional when running `generate`

- GENERATE_OWNER_KEY={private keys 302___}
- GENERATE_SUPPLY_MANAGER_KEY={private keys 302___}
- GENERATE_COMPLIANCE_MANAGER_KEY={private keys 302___}
- GENERATE_ENFORCEMENT_MANAGER_KEY={private keys 302___}

## Run

```
java -jar build/libs/stable-coin-0.2.0.jar
```

Note the `topic id` output to the console on first run and update `.env` file accordingly for subsequent runs.

## API

The current state of the contract is available via the State API.
By default, this is exposed at port 9000 and can be changed via the `HSC_STATE_PORT` variable.

##### Get Token Information

```
http GET :9000/
```

```json
{
    "complianceManager": "cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dc",
    "decimals": "2",
    "name": "Fire",
    "owner": "cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dc",
    "proposedOwner": "0000000000000000000000000000000000000000000000000000000000000000",
    "supplyManager": "cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dc",
    "symbol": "XFR",
    "totalSupply": "10000"
}
```

##### Get Address

```
http GET :9000/:address
http GET :9000/cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dc
```

```json
{
    "balance": "10000",
    "isFrozen": false,
    "isKycPassed": true
}
```

##### Get Allowance for Address

```
http GET :9000/:address/allowance/:other
http GET :9000/cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dc/allowance/cf750f7c2adc1baf87b3de9df1c317a5f845c994d0b28a6ec4655e7fc59b75dd
```

```json
{
    "allowance": "0"
}
```


## License

Licensed under Apache License,
Version 2.0 – see [LICENSE](LICENSE) in this repo
or [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).
