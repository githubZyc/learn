package com.example.springaialibaba.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExerciseMethod implements TeachingMethod {

    private final DashScopeChatModel dashScopeChatModel;
    private final ConversationManager conversationManager;
    private final ImageGenerationService imageGenerationService;

    @Autowired
    public ExerciseMethod(DashScopeChatModel dashScopeChatModel, 
                         ConversationManager conversationManager,
                         ImageGenerationService imageGenerationService) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.conversationManager = conversationManager;
        this.imageGenerationService = imageGenerationService;
    }

    @Override
    public String getName() {
        return "exercise";
    }

    @Override
    public String getDescription() {
        return "生成练习题";
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
                // Try to get content
                if (response.getResult().getOutput().getText() != null) {
                    return response.getResult().getOutput().getText();
                }
            }
        }
        
        return null;
    }

    @Override
    public Mono<List<ChatResponse>> execute(String input) {
        // This method is now deprecated, but kept for backward compatibility
        return executeWithSession(input, "default");
    }
    
    public Mono<List<ChatResponse>> executeWithSession(String input, String sessionId) {
        // Add user message to conversation history
        conversationManager.addUserMessage(sessionId, input);
        
        // Get conversation history (limit to last 10 messages to prevent context overflow)
        List<Message> history = new ArrayList<>();
        String systemMessage = "你是一个小学1-6年级的智能辅导老师。请为学生生成与知识点相关的练习题，并提供详细的解题过程和答案。请参考之前的对话历史来提供连贯的解答。" +
                              "在适当的时候，你可以创建图文结合的题目来增强学习体验。如果需要生成图片，请在回答中包含[IMAGE_PLACEHOLDER]标记。";
        history.add(new SystemMessage(systemMessage));
        history.addAll(conversationManager.getRecentMessages(sessionId, 10));
        
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
                            String imageUrl = imageGenerationService.generateMathProblemImage(input, sessionId);
                            if (imageUrl != null) {
                                // Replace placeholder with actual image URL
                                fullResponse = fullResponse.replace("[IMAGE_PLACEHOLDER]", 
                                    "[IMAGE_URL:" + imageUrl + "]");
                            }
                        } catch (Exception e) {
                            // Log the error but continue with the text response
                            System.err.println("Error generating image in ExerciseMethod: " + e.getMessage());
                            e.printStackTrace();
                            // Remove the placeholder so the user doesn't see it
                            fullResponse = fullResponse.replace("[IMAGE_PLACEHOLDER]", "");
                        }
                    }
                    
                    conversationManager.addAssistantMessage(sessionId, fullResponse);
                })
                .onErrorReturn(java.util.Collections.emptyList());
    }
}