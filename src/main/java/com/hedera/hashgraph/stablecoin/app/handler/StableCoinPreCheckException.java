package com.hedera.hashgraph.stablecoin.app.handler;

import com.hedera.hashgraph.stablecoin.app.Status;

public final class StableCoinPreCheckException extends Exception {
    public final Status status;

    public StableCoinPreCheckException(Status status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return "pre-check failed with status " + status;
    }
}
