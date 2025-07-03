package com.investbank.signals;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SignalProcessor {
    private final long cooldownMillis;
    private final Map<String, Long> lastSignalTime = new ConcurrentHashMap<>();
    private final Set<String> recentEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final SignalConsumer consumer;
    private final long dedupWindowMillis;

    public SignalProcessor(long cooldownMillis, long dedupWindowMillis, SignalConsumer consumer) {
        this.cooldownMillis = cooldownMillis;
        this.dedupWindowMillis = dedupWindowMillis;
        this.consumer = consumer;
    }

    public void process(SignalGenerator.Signal signal) {
        String key = dedupKey(signal);
        long now = System.currentTimeMillis();
        // Cooldown check
        if (lastSignalTime.containsKey(key) && now - lastSignalTime.get(key) < cooldownMillis) {
            return;
        }
        // De-duplication check
        if (recentEvents.contains(key)) {
            return;
        }
        // Forward clean signal
        consumer.consume(signal);
        lastSignalTime.put(key, now);
        recentEvents.add(key);
        // Schedule removal from dedup set
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                recentEvents.remove(key);
            }
        }, dedupWindowMillis);
    }

    private String dedupKey(SignalGenerator.Signal signal) {
        // Use type and direction for deduplication
        return signal.type + ":" + (signal.value > 0 ? "UP" : "DOWN");
    }

    public interface SignalConsumer {
        void consume(SignalGenerator.Signal signal);
    }
}
