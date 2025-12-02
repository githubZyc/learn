package com.example.springaialibaba.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ExplanationMethod implements TeachingMethod {

    private final DashScopeChatModel dashScopeChatModel;

    @Autowired
    public ExplanationMethod(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @Override
    public String getName() {
        return "explain";
    }

    @Override
    public String getDescription() {
        return "详细解释知识点";
    }

    @Override
    public Mono<List<ChatResponse>> execute(String input) {
        String systemMessage = "你是一个小学1-6年级的智能辅导老师。请用简单易懂的语言详细解释学生提出的知识点，适合小学生理解。";
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new UserMessage(input)));
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        return responseFlux.collectList()
                .onErrorReturn(java.util.Collections.emptyList());
    }
}