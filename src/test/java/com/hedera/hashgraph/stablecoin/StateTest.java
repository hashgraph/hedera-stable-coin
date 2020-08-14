package com.hedera.hashgraph.stablecoin;

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
        Assertions.assertEquals(new BigInteger("0"), state.getTokenDecimal());

        // TotalSupply : Int = 0
        Assertions.assertEquals(new BigInteger("0"), state.getTotalSupply());

        // Owner : Address = 0x
        Assertions.assertSame(Address.ZERO, state.getOwner());

        // SupplyManager: Address = {}
        Assertions.assertNull(state.getSupplyManager());

        // AssetProtectionManager: Address = {}
        Assertions.assertNull(state.getAssetProtectionManager());

        // Balances: Map::Address->Int = {}
        // TODO: Need to inspect private map here

        // Allowances: Map::Address->(Map::Address->Int) = {}
        // TODO: Need to inspect private map here

        // Frozen: Map::Address->Bool = {}
        // TODO: Need to inspect private map here

        // KycPassed: Map::Address->Bool = {}
        // TODO: Need to inspect private map here

        // ProposedOwner: Address = 0x
        Assertions.assertSame(Address.ZERO, state.getProposedOwner());

    }
}
