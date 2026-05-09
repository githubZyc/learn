package com.crawl.qeecc.config;

import com.crawl.qeecc.tool.SongSearchFunction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 模型层配置
 * 配置 LLM (ChatClient) 和 Embedding 模型
 */
@Configuration
public class AgentConfig {

    @Value("${agent.system.prompt}")
    private String systemPrompt;

    /**
     * 配置 ChatClient (通义千问 LLM)
     * 注册歌曲搜索 Function，实现 Function Calling 能力
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(systemPrompt)
                .defaultFunctions("songSearchFunction")
                .build();
    }
}
