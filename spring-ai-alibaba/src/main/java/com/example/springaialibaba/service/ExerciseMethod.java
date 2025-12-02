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
public class ExerciseMethod implements TeachingMethod {

    private final DashScopeChatModel dashScopeChatModel;

    @Autowired
    public ExerciseMethod(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @Override
    public String getName() {
        return "exercise";
    }

    @Override
    public String getDescription() {
        return "生成练习题";
    }

    @Override
    public Mono<List<ChatResponse>> execute(String input) {
        String systemMessage = "你是一个小学1-6年级的智能辅导老师。请为学生生成与知识点相关的练习题，并提供详细的解题过程和答案。";
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new UserMessage(input)));
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        return responseFlux.collectList()
                .onErrorReturn(java.util.Collections.emptyList());
    }
}