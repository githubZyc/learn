package com.example.springai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@SpringBootTest
class SpringAiApplicationTests {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private OpenAiChatModel chatModel;
    @Autowired
    private OpenAiImageModel openAiImageModel;

    @Test
    void contextLoads() {
    }


    @Test
    void testCallAi(){
        ChatClient.ChatClientPromptRequestSpec hello = chatClient.prompt(new Prompt("你好"));
        ChatClient.CallPromptResponseSpec call = hello.call();
        String s = call.chatResponse().toString();
        System.out.println(s);

        Map<String, String> generation = Map.of("generation", chatModel.call("你好"));
        System.out.println(generation);
    }

    @Test
    void testTextToImg(){
//        ImageResponse hd = openAiImageModel.call(new ImagePrompt("中国春节热闹的景象",
//                OpenAiImageOptions.builder()
//                        .withQuality("standard")
//                        .withN(1)
//                        .withHeight(1024)
//                        .withWidth(1024).build()));
        //[ImageGeneration{imageGenerationMetadata=OpenAiImageGenerationMetadata{revisedPrompt='A lively scene of the Chinese Spring Festival, filled with glowing red lanterns hanging along the streets, a parade of colourful dragons and lions danced by communities of different descents like Hispanic, South Asian, and Caucasian individuals in traditional costumes. Their genders varied as some were male dragon dancers and female lion dancers. Firecrackers explode in a safe distance, filling the air with a festive vibe. Street stalls set up by Middle Eastern and Black vendors display an array of traditional treats and handmade crafts. Families of diverse descents, all dressed up, wish each other good fortune for the New Year.'}, image=Image{url='https://oaidalleapiprodscus.blob.core.windows.net/private/org-cfyxOCia8zVghDsNfWGc3L3u/user-nkNLLtSjqFW0AkHaoGXoqFyt/img-UdqKW3BqHzlGAIRI3jZyL2Yf.png?st=2024-09-20T09%3A17%3A32Z&se=2024-09-20T11%3A17%3A32Z&sp=r&sv=2024-08-04&sr=b&rscd=inline&rsct=image/png&skoid=d505667d-d6c1-4a0a-bac7-5c84a87759f8&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-09-19T23%3A07%3A55Z&ske=2024-09-20T23%3A07%3A55Z&sks=b&skv=2024-08-04&sig=/0OQ%2B0m8RWoeyvwo1N4R8u9drrn4xmP428lXDsbNCT8%3D', b64Json='null'}}]]

//        ImageResponse hd = openAiImageModel.call(new ImagePrompt("雪山"));
        //ImageResponse [imageResponseMetadata=org.springframework.ai.image.ImageResponseMetadata@5521407f, imageGenerations=[ImageGeneration{imageGenerationMetadata=OpenAiImageGenerationMetadata{revisedPrompt='null'}, image=Image{url='https://oaidalleapiprodscus.blob.core.windows.net/private/org-ZFJSkGkhxfFrGUjR5PNYZYhf/user-2FY73MljmiI8nxEkLq6ygtVq/img-J9izmeCxWQKbOhuLEi7Jxtt4.png?st=2024-09-20T09%3A26%3A43Z&se=2024-09-20T11%3A26%3A43Z&sp=r&sv=2024-08-04&sr=b&rscd=inline&rsct=image/png&skoid=d505667d-d6c1-4a0a-bac7-5c84a87759f8&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-09-19T23%3A25%3A06Z&ske=2024-09-20T23%3A25%3A06Z&sks=b&skv=2024-08-04&sig=e6jPS1p%2B/xKhANGCKEDxUb6uftJPtH5kxaA0GsQvSrg%3D', b64Json='null'}}]]

        ImageResponse hd = openAiImageModel.call(new ImagePrompt("草原放牛"));
        //ImageResponse [imageResponseMetadata=org.springframework.ai.image.ImageResponseMetadata@71687d8f, imageGenerations=[ImageGeneration{imageGenerationMetadata=OpenAiImageGenerationMetadata{revisedPrompt='null'}, image=Image{url='https://oaidalleapiprodscus.blob.core.windows.net/private/org-yXjpE9SbALDnlCUgMteGQz5R/user-1muCR8nlmab9GHdSOv6tGjkq/img-JpKKLQWulDxRWi7ao4OuES8x.png?st=2024-09-20T09%3A28%3A45Z&se=2024-09-20T11%3A28%3A45Z&sp=r&sv=2024-08-04&sr=b&rscd=inline&rsct=image/png&skoid=d505667d-d6c1-4a0a-bac7-5c84a87759f8&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-09-20T02%3A40%3A07Z&ske=2024-09-21T02%3A40%3A07Z&sks=b&skv=2024-08-04&sig=bfAekxMg%2BTnGhgpbIdUVkysQyuiKyDWZH3YyIGEX11Q%3D', b64Json='null'}}, ImageGeneration{imageGenerationMetadata=OpenAiImageGenerationMetadata{revisedPrompt='null'}, image=Image{url='https://oaidalleapiprodscus.blob.core.windows.net/private/org-yXjpE9SbALDnlCUgMteGQz5R/user-1muCR8nlmab9GHdSOv6tGjkq/img-oRN16qgXxURiUG93x2pqsCYq.png?st=2024-09-20T09%3A28%3A45Z&se=2024-09-20T11%3A28%3A45Z&sp=r&sv=2024-08-04&sr=b&rscd=inline&rsct=image/png&skoid=d505667d-d6c1-4a0a-bac7-5c84a87759f8&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-09-20T02%3A40%3A07Z&ske=2024-09-21T02%3A40%3A07Z&sks=b&skv=2024-08-04&sig=29aM0p7iEZnYkXUy073V5I8VAEUnzYm6cg3T8BAf/2U%3D', b64Json='null'}}]]
        System.out.println(hd);
    }
}