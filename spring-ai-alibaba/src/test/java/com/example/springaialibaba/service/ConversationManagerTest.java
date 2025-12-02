package com.example.springaialibaba.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConversationManagerTest {

    @Test
    void testConversationManager() {
        ConversationManager manager = new ConversationManager();
        
        // Test adding and retrieving messages
        String sessionId = "test-session";
        manager.addUserMessage(sessionId, "Hello, teacher!");
        manager.addAssistantMessage(sessionId, "Hello! How can I help you today?");
        
        // Test getting conversation history
        var history = manager.getConversationHistory(sessionId);
        assertEquals(2, history.size());
        
        // Test getting recent messages
        var recent = manager.getRecentMessages(sessionId, 1);
        assertEquals(1, recent.size());
        
        // Test clearing conversation
        manager.clearConversation(sessionId);
        var clearedHistory = manager.getConversationHistory(sessionId);
        assertEquals(0, clearedHistory.size());
    }
}