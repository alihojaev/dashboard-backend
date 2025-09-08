package com.parser.core.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                "product-cache",
                "product-count-cache",
                "category-trees",
                "product-detail-cache",
                "product-entity-cache",
                "product-full-cache",
                "product-lang-cache",
                "product-setting-props-cache"
        ));
        return cacheManager;
    }
} 