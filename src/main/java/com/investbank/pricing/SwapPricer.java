package com.investbank.pricing;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class SwapPricer {
    public enum SwapType {
        OIS, LIBOR
    }

    public static class SwapLeg {
        public final List<Double> paymentTimes; // in years
        public final double notional;
        public final double fixedRate; // for fixed leg
        public final boolean isFixed;

        public SwapLeg(List<Double> paymentTimes, double notional, double fixedRate, boolean isFixed) {
            this.paymentTimes = paymentTimes;
            this.notional = notional;
            this.fixedRate = fixedRate;
            this.isFixed = isFixed;
        }
    }

    public static class Swap {
        public final SwapLeg fixedLeg;
        public final SwapLeg floatLeg;
        public final SwapType type;

        public Swap(SwapLeg fixedLeg, SwapLeg floatLeg, SwapType type) {
            this.fixedLeg = fixedLeg;
            this.floatLeg = floatLeg;
            this.type = type;
        }
    }

    private final CurveBootstrappingService curve;
    private final ReentrantLock lock = new ReentrantLock();

    public SwapPricer(CurveBootstrappingService curve) {
        this.curve = Objects.requireNonNull(curve);
    }

    // Present Value calculation (thread-safe)
    public double presentValue(Swap swap) {
        lock.lock();
        try {
            double pvFixed = legPV(swap.fixedLeg);
            double pvFloat = legPV(swap.floatLeg);
            return pvFloat - pvFixed; // payer swap: receive float, pay fixed
        } finally {
            lock.unlock();
        }
    }

    // Par rate calculation (thread-safe)
    public double parRate(Swap swap) {
        lock.lock();
        try {
            double floatPV = legPV(swap.floatLeg);
            double annuity = 0.0;
            for (double t : swap.fixedLeg.paymentTimes) {
                annuity += curve.getDiscountFactor(t);
            }
            return floatPV / (swap.fixedLeg.notional * annuity);
        } finally {
            lock.unlock();
        }
    }

    // DV01 (risk to 1bp move in rates)
    public double dv01(Swap swap) {
        lock.lock();
        try {
            double bump = 0.0001;
            double pvUp = presentValueWithBump(swap, bump);
            double pvDown = presentValueWithBump(swap, -bump);
            return (pvUp - pvDown) / 2.0;
        } finally {
            lock.unlock();
        }
    }

    // Helper: PV of a leg
    private double legPV(SwapLeg leg) {
        double pv = 0.0;
        for (double t : leg.paymentTimes) {
            double df = curve.getDiscountFactor(t);
            double cashflow = leg.isFixed ? leg.fixedRate * leg.notional : leg.notional; // Simplified float leg
            pv += cashflow * df;
        }
        return pv;
    }

    // Helper: PV with parallel bump to zero curve
    private double presentValueWithBump(Swap swap, double bump) {
        double pvFixed = 0.0, pvFloat = 0.0;
        for (double t : swap.fixedLeg.paymentTimes) {
            double r = curve.getZeroRate(t) + bump;
            double df = Math.exp(-r * t);
            pvFixed += swap.fixedLeg.fixedRate * swap.fixedLeg.notional * df;
        }
        for (double t : swap.floatLeg.paymentTimes) {
            double r = curve.getZeroRate(t) + bump;
            double df = Math.exp(-r * t);
            pvFloat += swap.floatLeg.notional * df;
        }
        return pvFloat - pvFixed;
    }
}
