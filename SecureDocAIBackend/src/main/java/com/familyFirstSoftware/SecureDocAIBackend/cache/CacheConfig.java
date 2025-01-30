package com.familyFirstSoftware.SecureDocAIBackend.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 1/26/2025
 *
 * Todo: create and use more cache beans
 */

@Configuration
public class CacheConfig {

    @Bean(name = "userLoginCache")
    public CacheStore<String, Integer> userCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }

    //TODO: this
    /*@Bean(name = "registrationCache")
    public CacheStore<String, Integer> registrationCache() {
        return new CacheStore<>(900, TimeUnit.SECONDS);
    }*/
}

