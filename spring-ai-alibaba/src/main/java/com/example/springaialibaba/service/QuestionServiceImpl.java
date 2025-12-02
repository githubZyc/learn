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
    private final ImageGenerationService imageGenerationService;

    @Autowired
    public QuestionServiceImpl(DashScopeChatModel dashScopeChatModel, 
                               ExplanationMethod explanationMethod,
                               ExampleMethod exampleMethod,
                               ExerciseMethod exerciseMethod,
                               ConversationManager conversationManager,
                               ImageGenerationService imageGenerationService) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.teachingMethods = new HashMap<>();
        this.teachingMethods.put(explanationMethod.getName(), explanationMethod);
        this.teachingMethods.put(exampleMethod.getName(), exampleMethod);
        this.teachingMethods.put(exerciseMethod.getName(), exerciseMethod);
        this.conversationManager = conversationManager;
        this.imageGenerationService = imageGenerationService;
    }

    /**
     * Safely extract text content from a ChatResponse object
     * @param response The ChatResponse object
     * @return The extracted text content or null if not found
     */
    private String extractTextFromResponse(ChatResponse response) {
        if (response == null) {
            return null;
        }
        
        // Try different possible structures for the response
        if (response.getResult() != null) {
            if (response.getResult().getOutput() != null) {
                // Try to get text directly
                if (response.getResult().getOutput().getText() != null) {
                    return response.getResult().getOutput().getText();
                }
                // Try to get content
                if (response.getResult().getOutput().getText() != null) {
                    return response.getResult().getOutput().getText();
                }
            }
            // Try to get text directly from result
            if (response.getResult().getOutput().getText() != null) {
                return response.getResult().getOutput().getText();
            }
        }
        
        return null;
    }

    @Override
    public Mono<List<ChatResponse>> askQuestion(String question, String sessionId) {
        // Debug logging
        System.out.println("Processing question: " + question);
        System.out.println("Session ID: " + sessionId);
        
        // Add user message to conversation history
        conversationManager.addUserMessage(sessionId, question);
        
        // Get conversation history (limit to last 10 messages to prevent context overflow)
        List<Message> history = new ArrayList<>();
        history.add(conversationManager.getSystemMessage(false));
        history.addAll(conversationManager.getRecentMessages(sessionId, 10));
        
        System.out.println("Sending prompt with " + history.size() + " messages");
        
        Prompt prompt = new Prompt(history);
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        
        return responseFlux.collectList()
                .doOnNext(responses -> {
                    System.out.println("Received " + responses.size() + " responses");
                    
                    // Extract and save assistant response to conversation history
                    StringBuilder responseBuilder = new StringBuilder();
                    for (ChatResponse response : responses) {
                        if (response.getResult() != null && response.getResult().getOutput() != null) {
                            String content = response.getResult().getOutput().getText();
                            if (content != null) {
                                responseBuilder.append(content);
                            }
                        }
                    }
                    
                    String fullResponse = responseBuilder.toString();
                    System.out.println("Full response: " + fullResponse);
                    
                    // Check if we need to generate an image
                    if (fullResponse.contains("[IMAGE_PLACEHOLDER]")) {
                        System.out.println("Detected IMAGE_PLACEHOLDER, generating image...");
                        // Generate an image for the math problem
                        String imageUrl = imageGenerationService.generateMathProblemImage(question, sessionId);
                        if (imageUrl != null) {
                            // Replace placeholder with actual image URL
                            fullResponse = fullResponse.replace("[IMAGE_PLACEHOLDER]", 
                                "[IMAGE_URL:" + imageUrl + "]");
                        }
                    }
                    
                    conversationManager.addAssistantMessage(sessionId, fullResponse);
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
                            // Safely extract text content from the response
                            String textContent = extractTextFromResponse(response);
                            if (textContent != null) {
                                responseBuilder.append(textContent);
                            }
                        }
                        
                        String fullResponse = responseBuilder.toString();
                        
                        // Check if we need to generate an image
                        if (fullResponse.contains("[IMAGE_PLACEHOLDER]")) {
                            try {
                                // Generate an image for the math problem
                                String imageUrl = imageGenerationService.generateMathProblemImage(question, sessionId);
                                if (imageUrl != null) {
                                    // Replace placeholder with actual image URL
                                    fullResponse = fullResponse.replace("[IMAGE_PLACEHOLDER]", 
                                        "[IMAGE_URL:" + imageUrl + "]");
                                }
                            } catch (Exception e) {
                                // Log the error but continue with the text response
                                System.err.println("Error generating image in askQuestionWithImage: " + e.getMessage());
                                e.printStackTrace();
                                // Remove the placeholder so the user doesn't see it
                                fullResponse = fullResponse.replace("[IMAGE_PLACEHOLDER]", "");
                            }
                        }
                        
                        conversationManager.addAssistantMessage(sessionId, fullResponse);
                    })
                    .onErrorResume(throwable -> {
                        // Log the error and return empty list
                        System.err.println("Error in askQuestionWithImage: " + throwable.getMessage());
                        throwable.printStackTrace();
                        conversationManager.addAssistantMessage(sessionId, "抱歉，我在处理您的图片问题时遇到了一些困难。请稍后再试。");
                        return Mono.just(new ArrayList<>());
                    });
        } catch (Exception e) {
            System.err.println("Exception in askQuestionWithImage: " + e.getMessage());
            e.printStackTrace();
            conversationManager.addAssistantMessage(sessionId, "抱歉，我在处理您的图片问题时遇到了一些困难。请稍后再试。");
            return Mono.just(new ArrayList<>());
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