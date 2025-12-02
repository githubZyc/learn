package com.example.springaialibaba.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface QuestionService {
    Mono<List<ChatResponse>> askQuestion(String question, String sessionId);
    Mono<List<ChatResponse>> askQuestionWithImage(String question, MultipartFile image, String sessionId);
    Mono<List<ChatResponse>> askQuestionWithMethod(String question, String method, String sessionId);
}