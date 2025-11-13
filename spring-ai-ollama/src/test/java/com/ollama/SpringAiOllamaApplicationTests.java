package com.ollama;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SpringAiOllamaApplicationTests {

    @Test
    void contextLoads() {
    }


    @Autowired
    private OllamaChatModel chatModel;

    @Test
    void testChat(){
        ChatResponse res = chatModel.call(new Prompt("你好"));
        System.out.println(res.toString());
        System.out.println(res.getResult().getOutput());
//        String resStr = chatModel.call("北京天气如何");
//        System.out.println(resStr);

    }
}
