package com.investbank.autohedge;

import java.util.*;

public class HedgeEvaluator {
    public static class HedgeExecution {
        public final String orderId;
        public final String symbol;
        public final double executedQty;
        public final double executedPrice;
        public final double referencePrice;
        public final long timestamp;

        public HedgeExecution(String orderId, String symbol, double executedQty, double executedPrice,
                double referencePrice, long timestamp) {
            this.orderId = orderId;
            this.symbol = symbol;
            this.executedQty = executedQty;
            this.executedPrice = executedPrice;
            this.referencePrice = referencePrice;
            this.timestamp = timestamp;
        }
    }

    public static class HedgeMetrics {
        public final String orderId;
        public final double pnlImpact;
        public final double slippage;
        public final double fillRatio;
        public final long timestamp;

        public HedgeMetrics(String orderId, double pnlImpact, double slippage, double fillRatio, long timestamp) {
            this.orderId = orderId;
            this.pnlImpact = pnlImpact;
            this.slippage = slippage;
            this.fillRatio = fillRatio;
            this.timestamp = timestamp;
        }
    }

    private final List<HedgeMetrics> metricsLog = new ArrayList<>();

    // Evaluate hedge effectiveness and record metrics
    public HedgeMetrics evaluate(HedgeExecution exec, double intendedQty) {
        double pnlImpact = (exec.executedPrice - exec.referencePrice) * exec.executedQty;
        double slippage = exec.executedPrice - exec.referencePrice;
        double fillRatio = intendedQty == 0 ? 0 : exec.executedQty / intendedQty;
        HedgeMetrics metrics = new HedgeMetrics(exec.orderId, pnlImpact, slippage, fillRatio, exec.timestamp);
        metricsLog.add(metrics);
        return metrics;
    }

    // Retrieve all recorded metrics (for TCA ingestion)
    public List<HedgeMetrics> getMetricsLog() {
        return Collections.unmodifiableList(metricsLog);
    }
}
