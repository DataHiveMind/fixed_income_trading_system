package com.investbank.autohedge;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AutoHedgeManager {
    private final HedgeOrderManager hedgeOrderManager;
    private final BlockingQueue<FillEvent> fillEventQueue = new LinkedBlockingQueue<>();

    public AutoHedgeManager(HedgeOrderManager hedgeOrderManager) {
        this.hedgeOrderManager = hedgeOrderManager;
        // Start fill event processing thread
        new Thread(this::processFills, "AutoHedge-FillProcessor").start();
    }

    // Represents a fill event (stub)
    public static class FillEvent {
        public final String symbol;
        public final double filledQty;
        public final double fillPrice;

        public FillEvent(String symbol, double filledQty, double fillPrice) {
            this.symbol = symbol;
            this.filledQty = filledQty;
            this.fillPrice = fillPrice;
        }
    }

    // Subscribe to fill events (could be called by external trade system)
    public void onFill(FillEvent event) {
        fillEventQueue.offer(event);
    }

    // Main fill processing loop
    private void processFills() {
        while (true) {
            try {
                FillEvent event = fillEventQueue.take();
                double hedgeQty = computeHedgeSize(event);
                HedgeOrderManager.HedgeOrder hedgeOrder = new HedgeOrderManager.HedgeOrder(
                        generateOrderId(), event.symbol, hedgeQty, event.fillPrice);
                hedgeOrderManager.sendOrder(hedgeOrder);
            } catch (Exception e) {
                // Log error
            }
        }
    }

    // Compute hedge size (stub: 1:1 hedge)
    private double computeHedgeSize(FillEvent event) {
        return -event.filledQty; // Simple offsetting hedge
    }

    // Generate unique order ID (stub)
    private String generateOrderId() {
        return "HEDGE-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }
}
