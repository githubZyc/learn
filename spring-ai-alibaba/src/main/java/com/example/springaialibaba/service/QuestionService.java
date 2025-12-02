package com.example.springaialibaba.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

public interface QuestionService {
    Mono<List<ChatResponse>> askQuestion(String question);
    Mono<List<ChatResponse>> askQuestionWithImage(String question, MultipartFile image);
    Mono<List<ChatResponse>> askQuestionWithMethod(String question, String method);
}