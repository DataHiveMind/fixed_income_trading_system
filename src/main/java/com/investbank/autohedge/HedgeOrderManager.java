package com.investbank.autohedge;

import quickfix.*;
import java.util.*;
import java.util.concurrent.*;

public class HedgeOrderManager {
    public enum OrderState {
        NEW, SENT, ACKED, FILLED, REJECTED, TIMEOUT
    }

    public static class HedgeOrder {
        public final String orderId;
        public final String symbol;
        public final double qty;
        public final double price;
        public volatile OrderState state;
        public volatile long lastSentTime;

        public HedgeOrder(String orderId, String symbol, double qty, double price) {
            this.orderId = orderId;
            this.symbol = symbol;
            this.qty = qty;
            this.price = price;
            this.state = OrderState.NEW;
            this.lastSentTime = 0;
        }
    }

    private final SessionID sessionID;
    private final Map<String, HedgeOrder> orders = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long timeoutMillis = 5000;

    public HedgeOrderManager(SessionID sessionID) {
        this.sessionID = sessionID;
        scheduler.scheduleAtFixedRate(this::checkTimeouts, 1, 1, TimeUnit.SECONDS);
    }

    // Send order via QuickFIX/J
    public void sendOrder(HedgeOrder order) throws SessionNotFound {
        // ...build FIX message (stub)...
        // quickfix.Message fixMsg = ...
        // Session.sendToTarget(fixMsg, sessionID);
        order.state = OrderState.SENT;
        order.lastSentTime = System.currentTimeMillis();
        orders.put(order.orderId, order);
    }

    // Handle order ack/fill/reject from QuickFIX/J
    public void onOrderUpdate(String orderId, OrderState newState) {
        HedgeOrder order = orders.get(orderId);
        if (order != null) {
            order.state = newState;
        }
    }

    // Retransmit on timeout or reject
    private void checkTimeouts() {
        long now = System.currentTimeMillis();
        for (HedgeOrder order : orders.values()) {
            if ((order.state == OrderState.SENT && now - order.lastSentTime > timeoutMillis)
                    || order.state == OrderState.REJECTED) {
                try {
                    sendOrder(order); // Retransmit
                } catch (Exception e) {
                    // Log error
                }
            }
        }
    }

    // Get order state
    public OrderState getOrderState(String orderId) {
        HedgeOrder order = orders.get(orderId);
        return order != null ? order.state : null;
    }
}
