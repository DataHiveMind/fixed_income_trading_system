package com.investbank.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SwapPricerTest {
    @Test
    void testSwapPVAndParRate() {
        CurveBootstrappingService curve = new CurveBootstrappingService();
        curve.bootstrap(
                Arrays.asList(new CurveBootstrappingService.MarketQuote(1.0, 0.01)),
                Collections.emptyList(), Collections.emptyList());
        SwapPricer.SwapLeg fixed = new SwapPricer.SwapLeg(Arrays.asList(1.0), 1000000, 0.01, true);
        SwapPricer.SwapLeg floating = new SwapPricer.SwapLeg(Arrays.asList(1.0), 1000000, 0.0, false);
        SwapPricer swapPricer = new SwapPricer(curve);
        SwapPricer.Swap swap = new SwapPricer.Swap(fixed, floating, SwapPricer.SwapType.LIBOR);
        double pv = swapPricer.presentValue(swap);
        double par = swapPricer.parRate(swap);
        assertEquals(0.0, pv, 1e-2);
        assertEquals(0.01, par, 1e-4);
    }
}
