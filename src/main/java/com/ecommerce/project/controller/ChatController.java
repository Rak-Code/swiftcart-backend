package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ChatRequest;
import com.ecommerce.project.dto.ChatResponse;
import com.ecommerce.project.service.GroqChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Groq AI Chat API")
public class ChatController {

    private final GroqChatService groqChatService;

    @PostMapping
    @Operation(summary = "Send a message to Groq AI chatbot")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = groqChatService.chat(request);
        return ResponseEntity.ok(response);
    }
}
