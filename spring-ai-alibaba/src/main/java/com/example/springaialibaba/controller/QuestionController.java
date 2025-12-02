package com.example.springaialibaba.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final DashScopeChatModel dashScopeChatModel;

    @Autowired
    public QuestionController(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @PostMapping("/ask")
    public Mono<List<ChatResponse>> askQuestion(@RequestBody String question) {
        String systemMessage = "你是一个小学1-6年级的智能辅导老师，具备以下知识库能力：\n" +
                        "1. 数学运算（加减乘除、分数、小数）\n" +
                        "2. 几何图形认知\n" +
                        "3. 基础代数\n" +
                        "4. 应用题解析\n" +
                        "请根据学生的问题，详细解答并给出解题过程。";
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new UserMessage(question)));
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        return responseFlux.collectList()
                .onErrorReturn(java.util.Collections.emptyList()); // Return empty list on error
    }
}