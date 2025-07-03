package com.investbank.signals;

import java.util.*;
import java.util.Optional;

public class SpreadMomentumSignal implements SignalGenerator.SignalCalculator {
    private final int fastWindow;
    private final int slowWindow;
    private final double threshold;
    private final Deque<Double> spreadHistory = new ArrayDeque<>();
    private final Deque<Double> fastHistory = new ArrayDeque<>();
    private final Deque<Double> slowHistory = new ArrayDeque<>();

    public SpreadMomentumSignal(int fastWindow, int slowWindow, double threshold) {
        this.fastWindow = fastWindow;
        this.slowWindow = slowWindow;
        this.threshold = threshold;
    }

    @Override
    public Optional<SignalGenerator.Signal> calculate(SignalGenerator.MarketDataSnapshot snapshot) {
        // Assume snapshot contains a spread value: double spread
        Double spread = (Double) getField(snapshot, "spread");
        if (spread == null)
            return Optional.empty();
        spreadHistory.addLast(spread);
        fastHistory.addLast(spread);
        slowHistory.addLast(spread);
        if (fastHistory.size() > fastWindow)
            fastHistory.removeFirst();
        if (slowHistory.size() > slowWindow)
            slowHistory.removeFirst();
        if (slowHistory.size() < slowWindow)
            return Optional.empty();
        double fastMA = fastHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double slowMA = slowHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double prevFastMA = getPrevMA(fastHistory, fastWindow);
        double prevSlowMA = getPrevMA(slowHistory, slowWindow);
        boolean crossover = (prevFastMA <= prevSlowMA && fastMA > slowMA)
                || (prevFastMA >= prevSlowMA && fastMA < slowMA);
        if (crossover && Math.abs(fastMA - slowMA) >= threshold) {
            String dir = fastMA > slowMA ? "UP" : "DOWN";
            return Optional.of(new SignalGenerator.Signal(
                    "SpreadMomentum:" + dir,
                    fastMA - slowMA,
                    System.currentTimeMillis()));
        }
        return Optional.empty();
    }

    private double getPrevMA(Deque<Double> history, int window) {
        if (history.size() <= 1)
            return 0.0;
        List<Double> list = new ArrayList<>(history);
        if (list.size() < window + 1)
            return 0.0;
        return list.subList(0, list.size() - 1).stream().skip(Math.max(0, list.size() - window - 1))
                .mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // Helper to extract spread from snapshot via reflection (for demo)
    private Object getField(Object obj, String field) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
