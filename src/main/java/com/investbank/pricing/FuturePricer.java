package com.investbank.pricing;

public class FuturePricer {
    private final CurveBootstrappingService curve;

    public FuturePricer(CurveBootstrappingService curve) {
        this.curve = curve;
    }

    // Convert futures price to implied rate (e.g., Eurodollar: 100 - price)
    public double priceToImpliedRate(double price) {
        return 100.0 - price;
    }

    // Convert implied rate to futures price
    public double impliedRateToPrice(double rate) {
        return 100.0 - rate;
    }

    // Calculate margining P&L for a position
    public double marginPnL(double entryPrice, double exitPrice, int contractSize, int numContracts) {
        return (exitPrice - entryPrice) * contractSize * numContracts;
    }

    // Adjust futures price for convexity using the curve (simplified)
    public double convexityAdjustedPrice(double price, double start, double end) {
        double rStart = curve.getZeroRate(start);
        double rEnd = curve.getZeroRate(end);
        double convexityAdj = 0.5 * (rEnd - rStart) * (end - start); // Simplified convexity
        return price + convexityAdj;
    }
}
