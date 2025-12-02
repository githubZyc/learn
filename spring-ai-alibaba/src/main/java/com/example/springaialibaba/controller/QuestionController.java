package com.example.springaialibaba.controller;

import com.example.springaialibaba.service.QuestionService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/ask")
    public Mono<List<ChatResponse>> askQuestion(@RequestBody(required = false) String question, 
                                               @RequestParam(required = false, defaultValue = "default") String sessionId) {
        // Debug logging
        System.out.println("Received question: " + question);
        System.out.println("Received sessionId: " + sessionId);
        
        // Handle case where question might be in request parameter instead of body
        if (question == null || question.isEmpty()) {
            System.err.println("Warning: Received empty question");
            return Mono.just(java.util.Collections.emptyList());
        }
        return questionService.askQuestion(question, sessionId);
    }

    @PostMapping(value = "/ask-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<List<ChatResponse>> askQuestionWithImage(
            @RequestParam("question") String question,
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false, defaultValue = "default") String sessionId) {
        return questionService.askQuestionWithImage(question, image, sessionId);
    }

    @PostMapping("/ask-with-method")
    public Mono<List<ChatResponse>> askQuestionWithMethod(
            @RequestParam("question") String question,
            @RequestParam("method") String method,
            @RequestParam(required = false, defaultValue = "default") String sessionId) {
        return questionService.askQuestionWithMethod(question, method, sessionId);
    }
}