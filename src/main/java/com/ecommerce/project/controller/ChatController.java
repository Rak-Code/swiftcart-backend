package com.ecommerce.project.controller;

import com.ecommerce.project.dto.ChatRequest;
import com.ecommerce.project.dto.ChatResponse;
import com.ecommerce.project.service.GroqChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final GroqChatService groqChatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = groqChatService.chat(request);
        return ResponseEntity.ok(response);
    }
}
