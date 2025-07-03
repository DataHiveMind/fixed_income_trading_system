package com.investbank.infrastructure;

import com.github.benmanes.caffeine.cache.*;
import java.util.concurrent.TimeUnit;

public class CacheProvider<K, V> {
    private final Cache<K, V> cache;

    public CacheProvider(long maxSize, long ttlSeconds) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .build();
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }
}
