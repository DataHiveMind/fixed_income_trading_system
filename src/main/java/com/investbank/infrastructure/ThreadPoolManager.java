package com.investbank.infrastructure;

import java.util.concurrent.*;

public class ThreadPoolManager {
    public static ExecutorService newFixedThreadPool(int n, String name) {
        return Executors.newFixedThreadPool(n, r -> new Thread(r, name + "-worker"));
    }

    public static ScheduledExecutorService newScheduledPool(int n, String name) {
        return Executors.newScheduledThreadPool(n, r -> new Thread(r, name + "-sched"));
    }

    public static ExecutorService newWorkStealingPool(int n) {
        return Executors.newWorkStealingPool(n);
    }

    public static void shutdown(ExecutorService pool) {
        pool.shutdown();
    }
}
