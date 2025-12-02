package com.example.springaialibaba.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TeachingMethod {
    String getName();
    String getDescription();
    Mono<List<ChatResponse>> execute(String input);
}