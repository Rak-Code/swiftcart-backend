package com.ecommerce.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8081}")
    private String allowedOrigins;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse specific origins from properties and add wildcard patterns
        List<String> originPatterns = Arrays.asList(allowedOrigins.split(","));
        
        // Add wildcard patterns for Vercel, Railway, and Lovable
        List<String> allPatterns = new java.util.ArrayList<>(originPatterns);
        allPatterns.add("https://*.vercel.app");
        allPatterns.add("https://*.railway.app");
        allPatterns.add("https://*.lovable.app");
        allPatterns.add("http://localhost:*");
        
        // Hard-coded specific origins for guaranteed access
        allPatterns.add("https://swiftcart-ui.vercel.app/");
        allPatterns.add("https://swiftcart-backend-x4ku.onrender.com");
        
        // Use allowedOriginPatterns only (supports both exact matches and wildcards)
        configuration.setAllowedOriginPatterns(allPatterns);
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
