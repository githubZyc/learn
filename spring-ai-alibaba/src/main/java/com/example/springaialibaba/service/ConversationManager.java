package com.example.springaialibaba.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationManager {
    
    // Store conversation history for each session
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();
    
    // Default system message
    private static final String DEFAULT_SYSTEM_MESSAGE = 
        "你是一个小学1-6年级的智能辅导老师，具备以下知识库能力：\n" +
        "1. 数学运算（加减乘除、分数、小数）\n" +
        "2. 几何图形认知\n" +
        "3. 基础代数\n" +
        "4. 应用题解析\n" +
        "请根据学生的问题，详细解答并给出解题过程。";
        
    // System message for image analysis
    private static final String IMAGE_SYSTEM_MESSAGE = 
        "你是一个小学1-6年级的智能辅导老师，具备以下知识库能力：\n" +
        "1. 数学运算（加减乘除、分数、小数）\n" +
        "2. 几何图形认知\n" +
        "3. 基础代数\n" +
        "4. 应用题解析\n" +
        "5. 图像识别与分析\n" +
        "请根据学生的问题和提供的图片，详细解答并给出解题过程。";
    
    /**
     * Get conversation history for a session
     * @param sessionId Session identifier
     * @return List of messages in the conversation
     */
    public List<Message> getConversationHistory(String sessionId) {
        return conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }
    
    /**
     * Add a user message to the conversation history
     * @param sessionId Session identifier
     * @param message User message
     */
    public void addUserMessage(String sessionId, String message) {
        List<Message> history = getConversationHistory(sessionId);
        history.add(new UserMessage(message));
    }
    
    /**
     * Add an assistant message to the conversation history
     * @param sessionId Session identifier
     * @param message Assistant message
     */
    public void addAssistantMessage(String sessionId, String message) {
        List<Message> history = getConversationHistory(sessionId);
        history.add(new AssistantMessage(message));
    }
    
    /**
     * Get the system message for the conversation
     * @param withImage Whether the conversation involves image analysis
     * @return System message
     */
    public SystemMessage getSystemMessage(boolean withImage) {
        return new SystemMessage(withImage ? IMAGE_SYSTEM_MESSAGE : DEFAULT_SYSTEM_MESSAGE);
    }
    
    /**
     * Clear conversation history for a session
     * @param sessionId Session identifier
     */
    public void clearConversation(String sessionId) {
        conversationHistory.remove(sessionId);
    }
    
    /**
     * Get the last N messages from the conversation history
     * @param sessionId Session identifier
     * @param limit Number of messages to retrieve
     * @return List of recent messages
     */
    public List<Message> getRecentMessages(String sessionId, int limit) {
        List<Message> history = getConversationHistory(sessionId);
        int size = history.size();
        int fromIndex = Math.max(0, size - limit);
        return new ArrayList<>(history.subList(fromIndex, size));
    }
}