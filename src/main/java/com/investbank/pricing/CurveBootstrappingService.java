package com.investbank.pricing;

import java.util.*;

public class CurveBootstrappingService {
    // Represents a market quote (deposit, FRA, swap)
    public static class MarketQuote {
        public final double tenor; // in years
        public final double rate; // annualized rate

        public MarketQuote(double tenor, double rate) {
            this.tenor = tenor;
            this.rate = rate;
        }
    }

    // Represents a bootstrapped curve (tenor -> zero rate)
    private final NavigableMap<Double, Double> zeroCurve = new TreeMap<>();
    private final NavigableMap<Double, Double> discountCurve = new TreeMap<>();

    // Main bootstrapping method
    public void bootstrap(List<MarketQuote> depositQuotes, List<MarketQuote> fraQuotes, List<MarketQuote> swapQuotes) {
        // 1. Sort all quotes by tenor
        List<MarketQuote> allQuotes = new ArrayList<>();
        allQuotes.addAll(depositQuotes);
        allQuotes.addAll(fraQuotes);
        allQuotes.addAll(swapQuotes);
        allQuotes.sort(Comparator.comparingDouble(q -> q.tenor));

        // 2. Piecewise-linear interpolation for zero curve
        for (MarketQuote quote : allQuotes) {
            zeroCurve.put(quote.tenor, quote.rate); // Simplified: use rate as zero rate
        }
        // 3. Build discount curve from zero curve
        for (Map.Entry<Double, Double> entry : zeroCurve.entrySet()) {
            double t = entry.getKey();
            double r = entry.getValue();
            double df = Math.exp(-r * t);
            discountCurve.put(t, df);
        }
    }

    // Linear interpolation for zero rate at arbitrary tenor
    public double getZeroRate(double tenor) {
        if (zeroCurve.containsKey(tenor))
            return zeroCurve.get(tenor);
        Map.Entry<Double, Double> lower = zeroCurve.floorEntry(tenor);
        Map.Entry<Double, Double> higher = zeroCurve.ceilingEntry(tenor);
        if (lower == null || higher == null)
            throw new IllegalArgumentException("Tenor out of curve range");
        double t1 = lower.getKey(), r1 = lower.getValue();
        double t2 = higher.getKey(), r2 = higher.getValue();
        return r1 + (r2 - r1) * (tenor - t1) / (t2 - t1);
    }

    // Get discount factor at arbitrary tenor
    public double getDiscountFactor(double tenor) {
        double r = getZeroRate(tenor);
        return Math.exp(-r * tenor);
    }

    // Accessors for full curves
    public NavigableMap<Double, Double> getZeroCurve() {
        return zeroCurve;
    }

    public NavigableMap<Double, Double> getDiscountCurve() {
        return discountCurve;
    }
}
