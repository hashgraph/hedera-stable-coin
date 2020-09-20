package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.stablecoin.app.handler.StableCoinPreCheckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StableCoinPreCheckExceptionTest {
    @Test
    public void stableCoinPreCheckExceptionTest() {
        var ex = new StableCoinPreCheckException(Status.WIPE_VALUE_WOULD_RESULT_IN_NEGATIVE_BALANCE);
        Assertions.assertEquals("pre-check failed with status WIPE_VALUE_WOULD_RESULT_IN_NEGATIVE_BALANCE", ex.getMessage());
    }
}
