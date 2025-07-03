package com.investbank.autohedge;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AutoHedgeManager {
    private final HedgeOrderManager hedgeOrderManager;
    private final HedgeStrategy hedgeStrategy;
    private final HedgeEvaluator hedgeEvaluator;
    private final BlockingQueue<FillEvent> fillEventQueue = new LinkedBlockingQueue<>();

    public AutoHedgeManager(HedgeOrderManager hedgeOrderManager, HedgeStrategy hedgeStrategy,
            HedgeEvaluator hedgeEvaluator) {
        this.hedgeOrderManager = hedgeOrderManager;
        this.hedgeStrategy = hedgeStrategy;
        this.hedgeEvaluator = hedgeEvaluator;
        // Register fill listener
        this.hedgeOrderManager.setFillListener(this::onOrderFilled);
        // Start fill event processing thread
        new Thread(this::processFills, "AutoHedge-FillProcessor").start();
    }

    // Represents a fill event (stub)
    public static class FillEvent {
        public final String symbol;
        public final double filledQty;
        public final double fillPrice;
        public final double currentDelta; // For strategy
        public final double currentVega; // For strategy

        public FillEvent(String symbol, double filledQty, double fillPrice, double currentDelta, double currentVega) {
            this.symbol = symbol;
            this.filledQty = filledQty;
            this.fillPrice = fillPrice;
            this.currentDelta = currentDelta;
            this.currentVega = currentVega;
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
                List<HedgeStrategy.HedgeInstruction> instructions = hedgeStrategy.computeHedge(
                        event.currentDelta, event.currentVega, event.symbol);
                for (HedgeStrategy.HedgeInstruction instr : instructions) {
                    HedgeOrderManager.HedgeOrder hedgeOrder = new HedgeOrderManager.HedgeOrder(
                            generateOrderId(), instr.symbol, instr.qty, event.fillPrice);
                    hedgeOrderManager.sendOrder(hedgeOrder);
                }
            } catch (Exception e) {
                // Log error
            }
        }
    }

    // Called when a hedge order is filled
    private void onOrderFilled(HedgeOrderManager.HedgeOrder order, HedgeOrderManager.OrderState state) {
        // For demo: use order.qty as intendedQty, order.price as referencePrice, and
        // now as timestamp
        HedgeEvaluator.HedgeExecution exec = new HedgeEvaluator.HedgeExecution(
                order.orderId, order.symbol, order.qty, order.price, order.price, System.currentTimeMillis());
        hedgeEvaluator.evaluate(exec, order.qty);
    }

    // Generate unique order ID (stub)
    private String generateOrderId() {
        return "HEDGE-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }
}
