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
public class ExampleMethod implements TeachingMethod {

    private final DashScopeChatModel dashScopeChatModel;

    @Autowired
    public ExampleMethod(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
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
        String systemMessage = "你是一个小学1-6年级的智能辅导老师。请为学生提供与问题相关的具体示例，帮助他们更好地理解和掌握知识点。";
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new UserMessage(input)));
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        return responseFlux.collectList()
                .onErrorReturn(java.util.Collections.emptyList());
    }
}