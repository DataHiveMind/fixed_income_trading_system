package com.investbank.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class CurveBootstrappingServiceTest {
    @Test
    void testBootstrappedCurveAccuracy() {
        List<CurveBootstrappingService.MarketQuote> quotes = Arrays.asList(
                new CurveBootstrappingService.MarketQuote(0.5, 0.01),
                new CurveBootstrappingService.MarketQuote(1.0, 0.012),
                new CurveBootstrappingService.MarketQuote(2.0, 0.015));
        CurveBootstrappingService svc = new CurveBootstrappingService();
        svc.bootstrap(quotes, Collections.emptyList(), Collections.emptyList());
        assertEquals(0.01, svc.getZeroRate(0.5), 1e-6);
        assertEquals(0.012, svc.getZeroRate(1.0), 1e-6);
        assertEquals(0.015, svc.getZeroRate(2.0), 1e-6);
    }
}
