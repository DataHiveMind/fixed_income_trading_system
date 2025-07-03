package com.investbank.infrastructure;

import com.lmax.disruptor.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisruptorManager {
    private final int bufferSize;
    private final Disruptor<ObjectEvent> disruptor;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public DisruptorManager(int bufferSize, EventHandler<ObjectEvent> handler) {
        this.bufferSize = bufferSize;
        this.disruptor = new Disruptor<>(ObjectEvent::new, bufferSize, executor);
        this.disruptor.handleEventsWith(handler);
        this.disruptor.start();
    }

    public void publish(Object obj) {
        disruptor.publishEvent((event, seq) -> event.set(obj));
    }

    public void shutdown() {
        disruptor.shutdown();
        executor.shutdown();
    }

    public static class ObjectEvent {
        private Object obj;

        public void set(Object obj) {
            this.obj = obj;
        }

        public Object get() {
            return obj;
        }
    }
}
