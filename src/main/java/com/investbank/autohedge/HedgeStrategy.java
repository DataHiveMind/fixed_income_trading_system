package com.investbank.autohedge;

import java.util.*;

public class HedgeStrategy {
    public enum HedgeType {
        DELTA_NEUTRAL, VEGA_NEUTRAL
    }

    public static class HedgeParams {
        public final double deltaThreshold;
        public final double vegaThreshold;
        public final double deltaSensitivity;
        public final double vegaSensitivity;

        public HedgeParams(double deltaThreshold, double vegaThreshold, double deltaSensitivity,
                double vegaSensitivity) {
            this.deltaThreshold = deltaThreshold;
            this.vegaThreshold = vegaThreshold;
            this.deltaSensitivity = deltaSensitivity;
            this.vegaSensitivity = vegaSensitivity;
        }
    }

    public static class HedgeInstruction {
        public final String symbol;
        public final double qty;
        public final HedgeType type;

        public HedgeInstruction(String symbol, double qty, HedgeType type) {
            this.symbol = symbol;
            this.qty = qty;
            this.type = type;
        }
    }

    private final HedgeParams params;
    private final HedgeType hedgeType;

    public HedgeStrategy(HedgeParams params, HedgeType hedgeType) {
        this.params = params;
        this.hedgeType = hedgeType;
    }

    // Compute hedge based on exposures
    public List<HedgeInstruction> computeHedge(double currentDelta, double currentVega, String symbol) {
        List<HedgeInstruction> instructions = new ArrayList<>();
        if (hedgeType == HedgeType.DELTA_NEUTRAL && Math.abs(currentDelta) > params.deltaThreshold) {
            double qty = -currentDelta / params.deltaSensitivity;
            instructions.add(new HedgeInstruction(symbol, qty, HedgeType.DELTA_NEUTRAL));
        }
        if (hedgeType == HedgeType.VEGA_NEUTRAL && Math.abs(currentVega) > params.vegaThreshold) {
            double qty = -currentVega / params.vegaSensitivity;
            instructions.add(new HedgeInstruction(symbol, qty, HedgeType.VEGA_NEUTRAL));
        }
        return instructions;
    }
}
