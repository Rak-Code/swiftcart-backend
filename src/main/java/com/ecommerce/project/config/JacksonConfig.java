package com.ecommerce.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson Configuration for HTTP JSON Serialization
 * This ensures clean JSON responses without type information
 */
@Configuration
public class JacksonConfig {

    /**
     * Primary ObjectMapper bean for HTTP responses
     * This will be used by Spring MVC for all REST API responses
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        
        // Register JavaTimeModule for Java 8 date/time support
        mapper.registerModule(new JavaTimeModule());
        
        // Disable writing dates as timestamps - use ISO-8601 format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Disable empty beans failure
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        // IMPORTANT: Do NOT activate default typing for HTTP responses
        // This prevents the ["java.util.ArrayList", [...]] wrapping issue
        mapper.deactivateDefaultTyping();
        
        return mapper;
    }
}
