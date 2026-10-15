package com.example.talentbridge.shared.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(
        name = "spring.cache.type",
        havingValue = "redis",
        matchIfMissing = false
)
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password:}")
    private String redisPassword;

    // =====================================================================
    // 1. Connect to Redis
    //    - Define RedisConnectionFactory with host, port, password
    //    - This bean is shared by RedisTemplate & Spring Cache
    // =====================================================================
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        if (!redisPassword.isBlank()) {
            serverConfig.setPassword(RedisPassword.of(redisPassword));
        }

        return new LettuceConnectionFactory(serverConfig);
    }



    // =====================================================================
    // 3. Configure Spring Cache with Redis
    //    - Set default time-to-live for cache (TTL)
    //    - Only applies to caches using annotations (@Cacheable, @CacheEvict...)
    // =====================================================================
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues();
    }

    // =====================================================================
    // 4. Initialize CacheManager using Redis
    //    - Manage cache through Spring Cache (annotation)
    //    - Automatically apply the above configurations to all caches
    // =====================================================================
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory factory,
            RedisCacheConfiguration cacheConfiguration) {
        return RedisCacheManager.builder(factory)
                .cacheDefaults(cacheConfiguration)
                .transactionAware()
                .build();
    }
}
