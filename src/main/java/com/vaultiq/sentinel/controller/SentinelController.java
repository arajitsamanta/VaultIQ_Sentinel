package com.vaultiq.sentinel.controller;

import com.vaultiq.sentinel.dto.ChatRequest;
import com.vaultiq.sentinel.dto.GuardedChatResponse;
import com.vaultiq.sentinel.service.GuardedChatService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentinel/chat")
public class SentinelController {

    private final GuardedChatService chatService;

    public SentinelController(GuardedChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GuardedChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request.question());
    }
}
