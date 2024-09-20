package com.example.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2024/9/19 17:26
 */
@RestController
public class TestController {
    @Autowired
    private ChatClient chatClient;

    @GetMapping(value = "/callAi",produces = "text/plain")
    public String callAi(){
        ChatClient.ChatClientPromptRequestSpec hello = chatClient.prompt(new Prompt("你好"));
        String s = hello.call().chatResponse().toString();
        return "hello";
    }
}
