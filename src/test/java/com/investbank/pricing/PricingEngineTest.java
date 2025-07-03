package com.investbank.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PricingEngineTest {
    @Test
    void testDelegationAndCaching() {
        PricingEngine engine = new PricingEngine();
        SwapPricer.SwapLeg fixed = new SwapPricer.SwapLeg(Arrays.asList(1.0), 1000000, 0.01, true);
        SwapPricer.SwapLeg floating = new SwapPricer.SwapLeg(Arrays.asList(1.0), 1000000, 0.0, false);
        SwapPricer.Swap swap = new SwapPricer.Swap(fixed, floating, SwapPricer.SwapType.LIBOR);
        double pv1 = engine.priceSwap(swap);
        double pv2 = engine.priceSwap(swap);
        assertEquals(pv1, pv2);
    }
}
