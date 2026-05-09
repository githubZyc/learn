package com.crawl.qeecc.controller;

import com.crawl.qeecc.agent.QeeccAgentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 用户交互层 - Agent 对话控制器
 * 提供 Web 页面和 REST API 两种交互方式
 */
@Controller
public class AgentChatController {

    private final QeeccAgentService agentService;

    public AgentChatController(QeeccAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 智能体对话页面
     */
    @GetMapping("/agent")
    public String agentPage() {
        return "agent";
    }

    /**
     * Agent 对话 API
     * 接收用户消息，返回 Agent 编排后的回答
     */
    @PostMapping("/api/agent/chat")
    @ResponseBody
    public String chat(@RequestParam String message) {
        return agentService.chat(message);
    }
}
