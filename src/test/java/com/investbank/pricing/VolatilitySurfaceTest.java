package com.investbank.pricing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class VolatilitySurfaceTest {
    @Test
    void testSurfaceInterpolation() {
        VolatilitySurfaceBuilder builder = new VolatilitySurfaceBuilder();
        List<VolatilitySurfaceBuilder.VolQuote> quotes = Arrays.asList(
                new VolatilitySurfaceBuilder.VolQuote(1.0, 100, 0.2),
                new VolatilitySurfaceBuilder.VolQuote(1.0, 110, 0.22),
                new VolatilitySurfaceBuilder.VolQuote(2.0, 100, 0.25),
                new VolatilitySurfaceBuilder.VolQuote(2.0, 110, 0.27));
        builder.buildSurface(quotes);
        double vol = builder.getVol(1.5, 105);
        assertTrue(vol > 0.2 && vol < 0.27);
    }
}
