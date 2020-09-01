package com.hedera.hashgraph.stablecoin.app;

import com.hedera.hashgraph.stablecoin.sdk.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class StateTest {
    @Test
    public void stateTest() {
        State state = new State();

        //TokenName : String = “”
        Assertions.assertEquals("", state.getTokenName());

        // TokenSymbol : String = “”
        Assertions.assertEquals("",state.getTokenSymbol());

        // TokenDecimal : Int = 0
        Assertions.assertEquals(0, state.getTokenDecimal());

        // TotalSupply : Int = 0
        Assertions.assertEquals(new BigInteger("0"), state.getTotalSupply());

        // Owner : Address = 0x
        Assertions.assertSame(Address.ZERO, state.getOwner());

        // SupplyManager: Address = {}
        Assertions.assertSame(Address.ZERO, state.getSupplyManager());

        // AssetProtectionManager: Address = {}
        Assertions.assertSame(Address.ZERO, state.getAssetProtectionManager());

        // Balances: Map::Address->Int = {}
        Assertions.assertTrue(state.isBalanceEmpty());

        // Allowances: Map::Address->(Map::Address->Int) = {}
        Assertions.assertTrue(state.isAllowancesEmpty());

        // Frozen: Map::Address->Bool = {}
        Assertions.assertTrue(state.isFrozenEmpty());

        // KycPassed: Map::Address->Bool = {}
        Assertions.assertTrue(state.isKycPassedEmpty());

        // ProposedOwner: Address = 0x
        Assertions.assertSame(Address.ZERO, state.getProposedOwner());
    }
}
