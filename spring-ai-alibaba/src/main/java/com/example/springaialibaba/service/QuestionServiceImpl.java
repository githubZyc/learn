package com.example.springaialibaba.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.model.Media;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final DashScopeChatModel dashScopeChatModel;
    private final Map<String, TeachingMethod> teachingMethods;
    private final ConversationManager conversationManager;

    @Autowired
    public QuestionServiceImpl(DashScopeChatModel dashScopeChatModel, 
                               ExplanationMethod explanationMethod,
                               ExampleMethod exampleMethod,
                               ExerciseMethod exerciseMethod,
                               ConversationManager conversationManager) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.teachingMethods = new HashMap<>();
        this.teachingMethods.put(explanationMethod.getName(), explanationMethod);
        this.teachingMethods.put(exampleMethod.getName(), exampleMethod);
        this.teachingMethods.put(exerciseMethod.getName(), exerciseMethod);
        this.conversationManager = conversationManager;
    }

    @Override
    public Mono<List<ChatResponse>> askQuestion(String question, String sessionId) {
        // Add user message to conversation history
        conversationManager.addUserMessage(sessionId, question);
        
        // Get conversation history (limit to last 10 messages to prevent context overflow)
        List<Message> history = new ArrayList<>();
        history.add(conversationManager.getSystemMessage(false));
        history.addAll(conversationManager.getRecentMessages(sessionId, 10));
        
        Prompt prompt = new Prompt(history);
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        
        return responseFlux.collectList()
                .doOnNext(responses -> {
                    // Extract and save assistant response to conversation history
                    StringBuilder responseBuilder = new StringBuilder();
                    for (ChatResponse response : responses) {
                        if (response.getResult() != null && response.getResult().getOutput() != null) {
                            responseBuilder.append(response.getResult().getOutput().getText());
                        }
                    }
                    conversationManager.addAssistantMessage(sessionId, responseBuilder.toString());
                })
                .onErrorReturn(java.util.Collections.emptyList());
    }

    @Override
    public Mono<List<ChatResponse>> askQuestionWithImage(String question, MultipartFile image, String sessionId) {
        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            // Add user message with image to conversation history
            conversationManager.addUserMessage(sessionId, question);
            
            // Get conversation history (limit to last 10 messages to prevent context overflow)
            List<Message> history = new ArrayList<>();
            history.add(conversationManager.getSystemMessage(true));
            
            // Create media object for the image with Builder pattern
            MimeType mimeType = MimeType.valueOf(image.getContentType());
            Media media = Media.builder()
                    .mimeType(mimeType)
                    .data(base64Image)
                    .build();

            // Create user message with both text and image
            UserMessage userMessage = new UserMessage(question, List.of(media));
            history.add(userMessage);
            
            // Add recent conversation history
            history.addAll(conversationManager.getRecentMessages(sessionId, 8)); // Leave room for system and image messages

            Prompt prompt = new Prompt(history);
            Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
            
            return responseFlux.collectList()
                    .doOnNext(responses -> {
                        // Extract and save assistant response to conversation history
                        StringBuilder responseBuilder = new StringBuilder();
                        for (ChatResponse response : responses) {
                            if (response.getResult() != null && response.getResult().getOutput() != null) {
                                responseBuilder.append(response.getResult().getOutput().getText());
                            }
                        }
                        conversationManager.addAssistantMessage(sessionId, responseBuilder.toString());
                    })
                    .onErrorReturn(java.util.Collections.emptyList());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<List<ChatResponse>> askQuestionWithMethod(String question, String method, String sessionId) {
        TeachingMethod teachingMethod = teachingMethods.get(method);
        if (teachingMethod != null) {
            // For teaching methods, we delegate to the specific method implementation
            if (teachingMethod instanceof ExplanationMethod) {
                return ((ExplanationMethod) teachingMethod).executeWithSession(question, sessionId);
            } else if (teachingMethod instanceof ExampleMethod) {
                return ((ExampleMethod) teachingMethod).executeWithSession(question, sessionId);
            } else if (teachingMethod instanceof ExerciseMethod) {
                return ((ExerciseMethod) teachingMethod).executeWithSession(question, sessionId);
            }
        }
        
        // Fall back to default method if the specified method is not found
        return askQuestion(question, sessionId);
    }
}