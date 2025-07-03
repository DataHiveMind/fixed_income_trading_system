package com.investbank.signals;

import java.util.*;
import java.util.concurrent.*;

public class SignalGenerator {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<SignalCalculator> calculators;
    private final SignalPublisher publisher;
    private final MarketDataProvider marketDataProvider;
    private final long pollIntervalMs;

    public SignalGenerator(List<SignalCalculator> calculators, SignalPublisher publisher,
            MarketDataProvider marketDataProvider, long pollIntervalMs) {
        this.calculators = calculators;
        this.publisher = publisher;
        this.marketDataProvider = marketDataProvider;
        this.pollIntervalMs = pollIntervalMs;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::pollAndGenerate, 0, pollIntervalMs, TimeUnit.MILLISECONDS);
    }

    private void pollAndGenerate() {
        MarketDataSnapshot snapshot = marketDataProvider.getSnapshot();
        for (SignalCalculator calc : calculators) {
            Optional<Signal> signal = calc.calculate(snapshot);
            signal.ifPresent(publisher::publish);
        }
    }

    // --- Interfaces and stubs for integration ---
    public interface MarketDataProvider {
        MarketDataSnapshot getSnapshot();
    }

    public interface SignalCalculator {
        Optional<Signal> calculate(MarketDataSnapshot snapshot);
    }

    public interface SignalPublisher {
        void publish(Signal signal);
    }

    public static class MarketDataSnapshot {
        // Add fields for prices, volumes, etc.
    }

    public static class Signal {
        public final String type;
        public final double value;
        public final long timestamp;

        public Signal(String type, double value, long timestamp) {
            this.type = type;
            this.value = value;
            this.timestamp = timestamp;
        }
    }
}
