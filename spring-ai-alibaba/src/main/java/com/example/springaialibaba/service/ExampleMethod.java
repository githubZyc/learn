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
public class ExampleMethod implements TeachingMethod {

    private final DashScopeChatModel dashScopeChatModel;
    private final ConversationManager conversationManager;

    @Autowired
    public ExampleMethod(DashScopeChatModel dashScopeChatModel, ConversationManager conversationManager) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.conversationManager = conversationManager;
    }

    @Override
    public String getName() {
        return "example";
    }

    @Override
    public String getDescription() {
        return "提供相关示例";
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
        String systemMessage = "你是一个小学1-6年级的智能辅导老师。请为学生提供与问题相关的具体示例，帮助他们更好地理解和掌握知识点。请参考之前的对话历史来提供连贯的解答。";
        history.add(new SystemMessage(systemMessage));
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
}