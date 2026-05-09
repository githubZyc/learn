package com.crawl.qeecc.agent;

import com.crawl.qeecc.rag.SongRagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent 编排层
 * 协调 LLM、RAG 检索和 Function Calling 工具调用
 * 实现意图识别 -> 知识检索 -> 工具调用 -> 结果生成的完整 Agent 工作流
 */
@Service
public class QeeccAgentService {

    private final ChatClient chatClient;
    private final SongRagService songRagService;

    public QeeccAgentService(ChatClient chatClient, SongRagService songRagService) {
        this.chatClient = chatClient;
        this.songRagService = songRagService;
    }

    /**
     * Agent 对话入口
     * 1. RAG 检索相关歌曲作为上下文增强
     * 2. 调用 ChatClient（自动触发 Function Calling）
     * 3. 返回 LLM 生成的回答
     */
    public String chat(String userMessage) {
        // Step 1: RAG 检索 - 从向量存储中查找语义相关的歌曲
        List<SongRagService.SongResult> ragResults = songRagService.search(userMessage, 3);
        StringBuilder ragContext = new StringBuilder();
        if (!ragResults.isEmpty()) {
            ragContext.append("\n【知识库检索结果】\n");
            for (SongRagService.SongResult result : ragResults) {
                ragContext.append(String.format("- 歌曲: %s | 分类: %s | 相似度: %.4f\n",
                        result.title(), result.category(), result.score()));
            }
            ragContext.append("\n请结合以上检索结果回答用户问题。");
        }

        // Step 2: Agent 编排 - ChatClient 自动处理 Function Calling
        // Spring AI 会自动解析 LLM 的工具调用请求，执行 SongSearchFunction，再将结果返回给 LLM
        String augmentedMessage = userMessage + ragContext;

        System.out.println("[Agent] 用户输入: " + userMessage);
        System.out.println("[Agent] RAG 上下文长度: " + ragContext.length() + " 字符");

        String response = chatClient.prompt()
                .user(augmentedMessage)
                .call()
                .content();

        System.out.println("[Agent] LLM 回复: " + response);
        return response;
    }
}
