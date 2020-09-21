package com.hedera.hashgraph.stablecoin.app.api;

import com.hedera.hashgraph.stablecoin.app.Status;
import com.hedera.hashgraph.stablecoin.sdk.Address;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public class TransactionResponseItem {
    public Instant consensusAt = Instant.EPOCH;

    public String caller = "";

    public Status status = Status.OK;

    public String transaction = "";

    public JsonObject data = null;

    public TransactionResponseItem() {
        // don't remove
        // this is for Jackson
    }

    public TransactionResponseItem(Instant consensusAt, Status status, String transaction, Address caller, JsonObject data) {
        this.caller = caller.toString();
        this.consensusAt = consensusAt;
        this.transaction = transaction;
        this.data = data;
        this.status = status;
    }
}
