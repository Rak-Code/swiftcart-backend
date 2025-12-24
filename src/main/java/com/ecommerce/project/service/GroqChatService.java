package com.ecommerce.project.service;

import com.ecommerce.project.dto.ChatRequest;
import com.ecommerce.project.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqChatService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";
    private static final int MAX_TOKENS = 8000; // Increased token limit for more context
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatResponse chat(ChatRequest request) {
        try {
            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", DEFAULT_MODEL);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", request.getMessage())
            ));
            requestBody.put("max_tokens", MAX_TOKENS);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Make API call
            ResponseEntity<String> response = restTemplate.exchange(
                GROQ_API_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Parse response
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            String content = jsonResponse.path("choices").get(0).path("message").path("content").asText();
            String model = jsonResponse.path("model").asText();
            int tokens = jsonResponse.path("usage").path("total_tokens").asInt();

            return new ChatResponse(content, model, tokens);

        } catch (Exception e) {
            throw new RuntimeException("Error calling Groq API: " + e.getMessage(), e);
        }
    }
}
