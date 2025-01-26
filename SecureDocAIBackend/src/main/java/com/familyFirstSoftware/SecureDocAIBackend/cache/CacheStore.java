package com.familyFirstSoftware.SecureDocAIBackend.cache;

import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/26/2025
 *
 * generic class that takes a key and value type and logs any cache operations.
 */
@Slf4j
public class CacheStore<K, V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
               .expireAfterWrite(expiryDuration, timeUnit)
               .build();

    }
}
