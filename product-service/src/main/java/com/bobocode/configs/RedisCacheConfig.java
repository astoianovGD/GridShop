package com.bobocode.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Distributed Redis cache configuration for product-service.
 * Uses JSON serialization and a 10-minute default TTL, with graceful fallback
 * to the database if the Redis server is temporarily unreachable.
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

    /**
     * Cache name for individual products by ID.
     */
    public static final String PRODUCT_BY_ID_CACHE = "productById";

    /**
     * Cache name for the full active product list.
     */
    public static final String ALL_PRODUCTS_CACHE = "allProducts";

    /**
     * Cache name for the full category list.
     */
    public static final String CATEGORIES_CACHE = "categories";

    /**
     * Cache name for individual categories by ID.
     */
    public static final String CATEGORY_BY_ID_CACHE = "categoryById";

    private static final long DEFAULT_TTL_MINUTES = 10L;

    /**
     * Configures the {@link RedisCacheManager} with JSON value serialization and TTL.
     *
     * @param connectionFactory the Redis connection factory
     * @return the configured {@link RedisCacheManager}
     */
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(DEFAULT_TTL_MINUTES))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * Provides a fault-tolerant {@link CacheErrorHandler} so that if Redis is offline,
     * requests transparently fall back to the database instead of failing with 500 errors.
     *
     * @return resilient cache error handler
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(
                    final RuntimeException exception, final Cache cache, final Object key
            ) {
                log.warn("Redis cache GET failed for cache='{}', key='{}' (falling back to DB): {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(
                    final RuntimeException exception,
                    final Cache cache,
                    final Object key,
                    final Object value
            ) {
                log.warn("Redis cache PUT failed for cache='{}', key='{}': {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(
                    final RuntimeException exception, final Cache cache, final Object key
            ) {
                log.warn("Redis cache EVICT failed for cache='{}', key='{}': {}",
                        cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(
                    final RuntimeException exception, final Cache cache
            ) {
                log.warn("Redis cache CLEAR failed for cache='{}': {}",
                        cache.getName(), exception.getMessage());
            }
        };
    }
}
