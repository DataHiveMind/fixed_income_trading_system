package com.investbank.autohedge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HedgeStrategyTest {
    @Test
    void testDeltaHedgeComputation() {
        HedgeStrategy.HedgeParams params = new HedgeStrategy.HedgeParams(0.1, 0.1, 2.0, 1.0);
        HedgeStrategy strategy = new HedgeStrategy(params, HedgeStrategy.HedgeType.DELTA_NEUTRAL);
        var result = strategy.computeHedge(0.5, 0.0, "SYM");
        assertEquals(1, result.size());
        assertEquals(-0.25, result.get(0).qty, 1e-6);
    }
}
