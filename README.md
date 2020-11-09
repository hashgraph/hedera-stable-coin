# Hedera Stable Coin

## Prerequisites

 - Java 14+

 - PostgreSQL 12+

 - [TimescaleDB](https://www.timescale.com/)

_see [install prerequisites](#install-prerequisites) at the end of this document for assistance if needed_

## Build

```shell script
# Create the development database. Assumes that TimescaleDB is running on `localhost:5432`
sudo su -l postgres
createdb -h localhost -U postgres stable_coin
# CTRL+D to exit postgres user shell

# Install git
sudo apt install git

# Clone the repository
git clone https://github.com/hashgraph/hedera-stable-coin.git

cd hedera-stable-coin

# Run database migrations.
./gradlew flywayMigrate

# Assemble JAR.
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

## Install prerequisites

Note: These installation instructions are applicable to `Debian 4.19`, other operating systems may differ.

### Java 14+

```shell script
# update and upgrade apt
sudo apt upgrade
sudo apt update

# install curl and wget
sudo apt -y install wget curl

# download JDK14
curl -O https://download.java.net/java/GA/jdk14/076bab302c7b4508975440c56f6cc26a/36/GPL/openjdk-14_linux-x64_bin.tar.gz

# Extract
tar xvf openjdk-14_linux-x64_bin.tar.gz

# Move to /opt
sudo mv jdk-14 /opt/

# Setup environment
sudo nano /etc/profile.d/jdk14.sh
export JAVA_HOME=/opt/jdk-14
export PATH=$PATH:$JAVA_HOME/bin
# save file

# Source profile
source /etc/profile.d/jdk14.sh

# Confirm java version
echo $JAVA_HOME
/opt/jdk-14

java -version
openjdk version "14" 2020-03-17
OpenJDK Runtime Environment (build 14+36-1461)
OpenJDK 64-Bit Server VM (build 14+36-1461, mixed mode, sharing)
```

### TimescaleDB

(instructions from https://docs.timescale.com/latest/getting-started/installation/debian/installation-apt-debian)

_Note: Assumes wget is installed_

```shell script
# `lsb_release -c -s` should return the correct codename of your OS
echo "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -c -s)-pgdg main" | sudo tee /etc/apt/sources.list.d/pgdg.list
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update

# Add timescale repository
sudo sh -c "echo 'deb https://packagecloud.io/timescale/timescaledb/debian/ `lsb_release -c -s` main' > /etc/apt/sources.list.d/timescaledb.list"
wget --quiet -O - https://packagecloud.io/timescale/timescaledb/gpgkey | sudo apt-key add -
sudo apt-get update

# Now install appropriate package for PG version
sudo apt-get install timescaledb-postgresql-12

# Configure the database, respond "y" to prompts
sudo timescaledb-tune

# Restart PostgreSQL instance
sudo service postgresql restart

# Set password for postgres user
sudo passwd postgres

# Connect to the database
sudo su -l postgres
psql

# psql (12.4 (Debian 12.4-1.pgdg100+1))
# Type "help" for help.

# Set postgres user password to 'password'
alter user postgres with password 'password';

CTRL+D to exit psql

CTRL+D again to return to your user shell
```

## License

Licensed under Apache License,
Version 2.0 – see [LICENSE](LICENSE) in this repo
or [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).
