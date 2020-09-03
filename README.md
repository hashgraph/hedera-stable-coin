# Hedera Stable Coin

## Prerequisites

 - Java 14+

 - PostgreSQL 12+

 - [TimescaleDB](https://www.timescale.com/)

## Build

Create the development database. Assumes that TimescaleDB is running on `localhost:5432`.

```
createdb -h localhost -U postgres stable_coin_dev
```

Run database migrations.

```
./gradlew flywayMigrate
```

Assemble JAR.

```
./gradlew assemble
```

## Run

```
java -jar build/libs/stable-coin-0.0.1-all.jar
```

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
