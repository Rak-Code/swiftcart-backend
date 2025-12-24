package com.ecommerce.project.config;

/*
 * REDIS TEST DISABLED
 * 
 * This test has been commented out because Redis has been removed from the application.
 * Uncomment this file if you re-enable Redis caching.
 */

/*
import com.ecommerce.project.dto.ProductResponseDTO;
import com.ecommerce.project.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.data.redis.password=",
    "spring.data.redis.ssl.enabled=false"
})
class RedisCacheTest {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired(required = false)
    private ObjectMapper redisObjectMapper;

    @Test
    void testRedisBeansAreConfigured() {
        assertNotNull(redisObjectMapper, "Redis ObjectMapper should be configured");
    }

    @Test
    void testProductResponseDTOSerialization() throws Exception {
        if (redisTemplate == null) {
            System.out.println("Redis not available, skipping test");
            return;
        }

        ProductResponseDTO product = new ProductResponseDTO(
                "test-id-123",
                "Test Product",
                "Test Description",
                99.99,
                10,
                "category-1",
                "Red",
                Product.Size.M,
                Arrays.asList("https://example.com/image1.jpg", "https://example.com/image2.jpg")
        );

        String key = "test:product:123";
        
        try {
            redisTemplate.opsForValue().set(key, product);
            Object retrieved = redisTemplate.opsForValue().get(key);
            assertNotNull(retrieved, "Retrieved object should not be null");
            redisTemplate.delete(key);
            System.out.println("Redis serialization test passed!");
        } catch (Exception e) {
            System.err.println("Redis test failed: " + e.getMessage());
        }
    }

    @Test
    void testListSerialization() throws Exception {
        if (redisTemplate == null) {
            System.out.println("Redis not available, skipping test");
            return;
        }

        List<ProductResponseDTO> products = Arrays.asList(
                new ProductResponseDTO(
                        "id-1",
                        "Product 1",
                        "Description 1",
                        49.99,
                        5,
                        "cat-1",
                        "Blue",
                        Product.Size.L,
                        List.of("https://example.com/img1.jpg")
                ),
                new ProductResponseDTO(
                        "id-2",
                        "Product 2",
                        "Description 2",
                        79.99,
                        3,
                        "cat-2",
                        "Green",
                        Product.Size.XL,
                        List.of("https://example.com/img2.jpg")
                )
        );

        String key = "test:products:list";
        
        try {
            redisTemplate.opsForValue().set(key, products);
            Object retrieved = redisTemplate.opsForValue().get(key);
            assertNotNull(retrieved, "Retrieved list should not be null");
            redisTemplate.delete(key);
            System.out.println("Redis list serialization test passed!");
        } catch (Exception e) {
            System.err.println("Redis list test failed: " + e.getMessage());
        }
    }
}
*/
