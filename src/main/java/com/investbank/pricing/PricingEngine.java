package com.investbank.pricing;

import java.util.*;
import com.investbank.infrastructure.ConfigLoader;

public class PricingEngine {
    private final CurveBootstrappingService curveService;
    private final SwapPricer swapPricer;
    private final FuturePricer futurePricer;
    private final VolatilitySurfaceBuilder volSurfaceBuilder;
    private final Map<String, Object> cache = new HashMap<>();

    public PricingEngine() {
        // Load config (stub: replace with actual config usage as needed)
        Map<String, Object> config = ConfigLoader.load("pricing.yml");
        this.curveService = new CurveBootstrappingService();
        this.swapPricer = new SwapPricer(curveService);
        this.futurePricer = new FuturePricer(curveService);
        this.volSurfaceBuilder = new VolatilitySurfaceBuilder();
    }

    // Swap pricing facade
    public double priceSwap(SwapPricer.Swap swap) {
        String key = "swap:" + swap.hashCode();
        return (double) cache.computeIfAbsent(key, k -> swapPricer.presentValue(swap));
    }

    public double swapParRate(SwapPricer.Swap swap) {
        String key = "swapParRate:" + swap.hashCode();
        return (double) cache.computeIfAbsent(key, k -> swapPricer.parRate(swap));
    }

    public double swapDV01(SwapPricer.Swap swap) {
        String key = "swapDV01:" + swap.hashCode();
        return (double) cache.computeIfAbsent(key, k -> swapPricer.dv01(swap));
    }

    // Futures pricing facade
    public double futuresImpliedRate(double price) {
        String key = "futImpliedRate:" + price;
        return (double) cache.computeIfAbsent(key, k -> futurePricer.priceToImpliedRate(price));
    }

    public double futuresPrice(double rate) {
        String key = "futPrice:" + rate;
        return (double) cache.computeIfAbsent(key, k -> futurePricer.impliedRateToPrice(rate));
    }

    public double futuresMarginPnL(double entry, double exit, int size, int n) {
        String key = "futPnL:" + entry + ":" + exit + ":" + size + ":" + n;
        return (double) cache.computeIfAbsent(key, k -> futurePricer.marginPnL(entry, exit, size, n));
    }

    // Volatility surface facade
    public void buildVolSurface(List<VolatilitySurfaceBuilder.VolQuote> quotes) {
        volSurfaceBuilder.buildSurface(quotes);
    }

    public double getImpliedVol(double expiry, double strike) {
        String key = "vol:" + expiry + ":" + strike;
        return (double) cache.computeIfAbsent(key, k -> volSurfaceBuilder.getVol(expiry, strike));
    }

    // Expose curve service for bootstrapping
    public CurveBootstrappingService getCurveService() {
        return curveService;
    }
}
