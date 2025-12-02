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
    public Mono<List<ChatResponse>> askQuestion(@RequestBody String question) {
        return questionService.askQuestion(question);
    }

    @PostMapping(value = "/ask-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<List<ChatResponse>> askQuestionWithImage(
            @RequestParam("question") String question,
            @RequestParam("image") MultipartFile image) {
        return questionService.askQuestionWithImage(question, image);
    }

    @PostMapping("/ask-with-method")
    public Mono<List<ChatResponse>> askQuestionWithMethod(
            @RequestParam("question") String question,
            @RequestParam("method") String method) {
        return questionService.askQuestionWithMethod(question, method);
    }
}