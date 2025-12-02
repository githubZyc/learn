package com.example.springaialibaba.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ImageGenerationService {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    private ImageSynthesis imageSynthesis;

    @PostConstruct
    public void init() {
        imageSynthesis = new ImageSynthesis();
    }

    /**
     * Generate an image based on text description
     * @param prompt Text description of the image to generate
     * @param sessionId Session identifier for tracking
     * @return Image URL
     * @throws ApiException
     * @throws InputRequiredException
     */
    public String generateImage(String prompt, String sessionId) 
            throws ApiException, InputRequiredException {
        
        // Validate API key
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("DashScope API key is not configured");
        }
        
        // Validate prompt
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }
        
        // Create image synthesis parameters
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(apiKey)
                .model("wanx-v1") // Use string literal for model name
                .prompt(prompt)
                .n(1) // Generate 1 image
                .size("256*256") // Image size
                .build();

        // Generate the image
        ImageSynthesisResult result = null;
        try {
            result = imageSynthesis.call(param);
        } catch (NoApiKeyException e) {
            e.printStackTrace();
        }

        // Extract image URL from result
        if (result != null && result.getOutput() != null && 
            result.getOutput().getResults() != null && 
            !result.getOutput().getResults().isEmpty()) {
            
            String imageUrl = result.getOutput().getResults().get(0).get("url");
            return imageUrl;
        }
        
        return null;
    }

    /**
     * Generate a math problem with image
     * @param problemDescription Description of the math problem
     * @param sessionId Session identifier
     * @return Generated image URL
     */
    public String generateMathProblemImage(String problemDescription, String sessionId) {
        try {
            // Validate input
            if (problemDescription == null || problemDescription.trim().isEmpty()) {
                return null;
            }
            
            String prompt = "Create a clear, educational illustration for a math problem: " + problemDescription + 
                           ". The image should be suitable for elementary school students (grades 1-6). " +
                           "Include visual elements like shapes, numbers, or objects that help illustrate the problem. " +
                           "Keep the design simple, colorful, and easy to understand.";
            
            return generateImage(prompt, sessionId);
        } catch (Exception e) {
            // Log the error and return null
            System.err.println("Error generating image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}