package com.familyFirstSoftware.SecureDocAIBackend.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/26/2025
 *
 * Generic class that takes a key and value type.
 * Wrapper around google common cache.
 *
 * expiryDuration: how many units of time before the cache expires
 * timeUnit: the unit of time that the expiryDuration is
 *
 * TODO: Play around with adding more things
 */
@Slf4j
public class CacheStore<K, V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
               .expireAfterWrite(expiryDuration, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
               .build();

    }

    public V get(@NotNull K key) {
        log.info("Retrieving from cache: {}", key.toString());
        return cache.getIfPresent(key);
    }

    public void put(@NotNull K key, @NotNull V value) {
        log.info("Adding to cache: {}", key.toString());
        cache.put(key, value);
    }

    public void evict(@NotNull K key) {
        log.info("Evicting from cache: {}", key.toString());
        cache.invalidate(key);
    }
}
