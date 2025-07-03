package com.investbank.signals;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SignalGeneratorTest {
    @Test
    void testSignalEmission() {
        List<SignalGenerator.Signal> published = new ArrayList<>();
        SignalGenerator.SignalPublisher publisher = published::add;
        SignalGenerator.MarketDataProvider provider = () -> new SignalGenerator.MarketDataSnapshot();
        SignalGenerator.SignalCalculator calc = snapshot -> Optional
                .of(new SignalGenerator.Signal("TEST", 1.0, System.currentTimeMillis()));
        SignalGenerator gen = new SignalGenerator(List.of(calc), publisher, provider, 1000);
        gen.start();
        try {
            Thread.sleep(1100);
        } catch (InterruptedException ignored) {
        }
        assertFalse(published.isEmpty());
    }
}
