package com.investbank.pricing;

import java.util.*;

public class VolatilitySurfaceBuilder {
    public static class VolQuote {
        public final double expiry; // in years
        public final double strike;
        public final double vol;

        public VolQuote(double expiry, double strike, double vol) {
            this.expiry = expiry;
            this.strike = strike;
            this.vol = vol;
        }
    }

    // 2D grid: expiry -> strike -> vol
    private final TreeMap<Double, TreeMap<Double, Double>> surface = new TreeMap<>();

    // Ingest vol quotes and build surface (SABR/SVI stubbed for now)
    public void buildSurface(List<VolQuote> quotes) {
        for (VolQuote q : quotes) {
            surface.computeIfAbsent(q.expiry, k -> new TreeMap<>()).put(q.strike, q.vol);
        }
    }

    // Bilinear interpolation for vol lookup
    public double getVol(double expiry, double strike) {
        Map.Entry<Double, TreeMap<Double, Double>> lowerExp = surface.floorEntry(expiry);
        Map.Entry<Double, TreeMap<Double, Double>> upperExp = surface.ceilingEntry(expiry);
        if (lowerExp == null || upperExp == null)
            throw new IllegalArgumentException("Expiry out of range");
        double e1 = lowerExp.getKey(), e2 = upperExp.getKey();
        TreeMap<Double, Double> srf1 = lowerExp.getValue(), srf2 = upperExp.getValue();
        double v11 = getStrikeVol(srf1, strike, true);
        double v12 = getStrikeVol(srf1, strike, false);
        double v21 = getStrikeVol(srf2, strike, true);
        double v22 = getStrikeVol(srf2, strike, false);
        double s1 = srf1.floorKey(strike), s2 = srf1.ceilingKey(strike);
        // Bilinear interpolation
        double w1 = (e2 - expiry) / (e2 - e1);
        double w2 = (expiry - e1) / (e2 - e1);
        double ws1 = (s2 - strike) / (s2 - s1);
        double ws2 = (strike - s1) / (s2 - s1);
        double v = w1 * (ws1 * v11 + ws2 * v12) + w2 * (ws1 * v21 + ws2 * v22);
        return v;
    }

    private double getStrikeVol(TreeMap<Double, Double> srf, double strike, boolean floor) {
        Double key = floor ? srf.floorKey(strike) : srf.ceilingKey(strike);
        if (key == null)
            throw new IllegalArgumentException("Strike out of range");
        return srf.get(key);
    }
}
