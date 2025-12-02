package com.example.springaialibaba.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.model.Media;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final DashScopeChatModel dashScopeChatModel;
    private final Map<String, TeachingMethod> teachingMethods;

    @Autowired
    public QuestionServiceImpl(DashScopeChatModel dashScopeChatModel, 
                               ExplanationMethod explanationMethod,
                               ExampleMethod exampleMethod,
                               ExerciseMethod exerciseMethod) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.teachingMethods = new HashMap<>();
        this.teachingMethods.put(explanationMethod.getName(), explanationMethod);
        this.teachingMethods.put(exampleMethod.getName(), exampleMethod);
        this.teachingMethods.put(exerciseMethod.getName(), exerciseMethod);
    }

    @Override
    public Mono<List<ChatResponse>> askQuestion(String question) {
        String systemMessage = "你是一个小学1-6年级的智能辅导老师，具备以下知识库能力：\n" +
                "1. 数学运算（加减乘除、分数、小数）\n" +
                "2. 几何图形认知\n" +
                "3. 基础代数\n" +
                "4. 应用题解析\n" +
                "请根据学生的问题，详细解答并给出解题过程。";
        Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), new UserMessage(question)));
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
        return responseFlux.collectList()
                .onErrorReturn(java.util.Collections.emptyList());
    }

    @Override
    public Mono<List<ChatResponse>> askQuestionWithImage(String question, MultipartFile image) {
        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(image.getBytes());

            String systemMessage = "你是一个小学1-6年级的智能辅导老师，具备以下知识库能力：\n" +
                    "1. 数学运算（加减乘除、分数、小数）\n" +
                    "2. 几何图形认知\n" +
                    "3. 基础代数\n" +
                    "4. 应用题解析\n" +
                    "5. 图像识别与分析\n" +
                    "请根据学生的问题和提供的图片，详细解答并给出解题过程。";

            // Create media object for the image with Builder pattern
            MimeType mimeType = MimeType.valueOf(image.getContentType());
            Media media = Media.builder()
                    .mimeType(mimeType)
                    .data(base64Image)
                    .build();

            // Create user message with both text and image
            UserMessage userMessage = new UserMessage(question, List.of(media));

            Prompt prompt = new Prompt(List.of(new SystemMessage(systemMessage), userMessage));
            Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(prompt);
            return responseFlux.collectList()
                    .onErrorReturn(java.util.Collections.emptyList());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<List<ChatResponse>> askQuestionWithMethod(String question, String method) {
        TeachingMethod teachingMethod = teachingMethods.get(method);
        if (teachingMethod != null) {
            return teachingMethod.execute(question);
        } else {
            // Fall back to default method if the specified method is not found
            return askQuestion(question);
        }
    }
}