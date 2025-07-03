package com.investbank.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FuturePricerTest {
    @Test
    void testPriceImpliedRateRoundTrip() {
        CurveBootstrappingService curve = new CurveBootstrappingService();
        FuturePricer pricer = new FuturePricer(curve);
        double price = 98.75;
        double rate = pricer.priceToImpliedRate(price);
        assertEquals(1.25, rate, 1e-6);
        assertEquals(price, pricer.impliedRateToPrice(rate), 1e-6);
    }
}
