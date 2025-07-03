package com.investbank.signals;

import java.util.*;
import java.util.Optional;

public class CurveShiftSignal implements SignalGenerator.SignalCalculator {
    private final Map<Double, Double> historicalCurve; // tenor -> zero rate
    private final double threshold; // minimum shift to trigger signal

    public CurveShiftSignal(Map<Double, Double> historicalCurve, double threshold) {
        this.historicalCurve = new HashMap<>(historicalCurve);
        this.threshold = threshold;
    }

    @Override
    public Optional<SignalGenerator.Signal> calculate(SignalGenerator.MarketDataSnapshot snapshot) {
        // Try to extract 'curve' field from snapshot
        Map<Double, Double> currentCurve = Collections.emptyMap();
        Object field = getField(snapshot, "curve");
        if (field instanceof Map) {
            try {
                currentCurve = (Map<Double, Double>) field;
            } catch (ClassCastException e) {
                // Ignore, use empty map
            }
        }
        double maxShift = 0.0;
        double maxTenor = 0.0;
        double direction = 0.0;
        for (Map.Entry<Double, Double> entry : currentCurve.entrySet()) {
            double tenor = entry.getKey();
            double currRate = entry.getValue();
            double histRate = historicalCurve.getOrDefault(tenor, currRate);
            double shift = currRate - histRate;
            if (Math.abs(shift) > Math.abs(maxShift)) {
                maxShift = shift;
                maxTenor = tenor;
                direction = Math.signum(shift);
            }
        }
        if (Math.abs(maxShift) >= threshold) {
            return Optional.of(new SignalGenerator.Signal(
                    "CurveShift:Tenor=" + maxTenor + ":Dir=" + (direction > 0 ? "UP" : "DOWN"),
                    maxShift,
                    System.currentTimeMillis()));
        }
        return Optional.empty();
    }

    // Helper to extract curve from snapshot via reflection (for demo)
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
