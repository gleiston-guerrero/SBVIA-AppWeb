package com.sbvia.backend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

/**
 * Redis cache configuration.
 *
 * Design decision (ADR-004):
 * - RedisCacheManager is used so that @Cacheable/@CacheEvict take Redis as their backend.
 * - JSON serialisation (GenericJackson2JsonRedisSerializer) instead of Java native serialisation
 *   to avoid ClassCastException across versions and to allow inspecting keys in redis-cli.
 * - Global TTL: 10 minutes. Specific TTL for "scenarios": 5 minutes (semi-static data).
 *
 * @author Keitho_
 */
@Configuration
@EnableCaching
public class CacheConfig {
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
    private static final Duration ESCENARIOS_TTL = Duration.ofMinutes(5);

    /**
     * Builds the {@link org.springframework.data.redis.cache.RedisCacheManager} that backs all
     * {@code @Cacheable} and {@code @CacheEvict} operations with Redis as the
     * cache backend. Values are serialized as JSON with
     * {@code GenericJackson2JsonRedisSerializer}, null values are never cached,
     * and entries expire after 10 minutes by default, except those in the
     * dedicated "scenarios" cache which expire after 5 minutes.
     *
     * @param connectionFactory the Redis connection factory used to connect to the Redis server
     * @return a configured {@link org.springframework.data.redis.cache.RedisCacheManager} with JSON serialization and per-cache TTLs
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default configuration: JSON + 10-minute TTL + no null-value caching
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        // Specific configuration for the scenarios cache: 5-minute TTL
        RedisCacheConfiguration escenariosConfig = defaultConfig
                .entryTtl(ESCENARIOS_TTL);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(
                        "scenarios", escenariosConfig
                ))
                .build();
    }
}
