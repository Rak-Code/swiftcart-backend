package com.ecommerce.project.config;

/*
 * REDIS CACHING DISABLED
 * 
 * This configuration has been commented out to remove Redis dependency.
 * The application will work without caching - all data will be fetched from MongoDB.
 * 
 * To re-enable Redis caching:
 * 1. Uncomment this entire file
 * 2. Uncomment Redis dependencies in pom.xml
 * 3. Uncomment Redis configuration in application.properties
 * 4. Uncomment cache annotations in ProductServiceImpl.java
 */

/*
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for LocalDateTime and other Java 8 time types
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Enable polymorphic type handling for proper deserialization
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        
        return mapper;
    }

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration(ObjectMapper redisObjectMapper) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer(redisObjectMapper)
                        )
                )
                .disableCachingNullValues();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, 
                                     RedisCacheConfiguration defaultCacheConfiguration) {
        
        // Custom TTL configurations for different caches
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Single product cache - 2 hours
        cacheConfigurations.put("product", 
                defaultCacheConfiguration.entryTtl(Duration.ofHours(2)));
        
        // All products list - 30 minutes (changes frequently)
        cacheConfigurations.put("productsAll", 
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        
        // Paginated products - 1 hour
        cacheConfigurations.put("productsPage", 
                defaultCacheConfiguration.entryTtl(Duration.ofHours(1)));
        
        // Products by category - 1 hour
        cacheConfigurations.put("productsByCategory", 
                defaultCacheConfiguration.entryTtl(Duration.ofHours(1)));
        
        // Search results - 30 minutes
        cacheConfigurations.put("productsBySearch", 
                defaultCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
*/
