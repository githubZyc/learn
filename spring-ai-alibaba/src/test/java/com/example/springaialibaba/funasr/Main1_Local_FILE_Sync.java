package com.example.springaialibaba.funasr;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.transcription.Transcription;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionQueryParam;
import com.alibaba.dashscope.audio.asr.transcription.TranscriptionResult;
import com.alibaba.dashscope.utils.Constants;
import com.alibaba.nacos.shaded.com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Arrays;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2025/11/12 17:06
 */
public class Main1_Local_FILE_Sync {
    public static void main(String[] args) {
        // 创建Recognition实例
        Recognition recognizer = new Recognition();
        // 创建RecognitionParam
        RecognitionParam param =
                RecognitionParam.builder()
                        // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将apiKey替换为自己的API Key
                        .apiKey("sk-66a54e2d1d7b40beac4da321fb67358b")
                        .model("fun-asr-realtime")
                        .format("wav")
                        .sampleRate(16000)
                        // “language_hints”只支持paraformer-realtime-v2模型
                        .parameter("language_hints", new String[]{"zh", "en"})
                        .build();

        try {
            System.out.println("识别结果：" + recognizer.call(param, new File("/Volumes/files/workspace/learn/spring-ai-alibaba/src/test/java/com/example/springaialibaba/funasr/hello_world_female2.wav")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 任务结束后关闭 WebSocket 连接
            recognizer.getDuplexApi().close(1000, "bye");
        }
        System.out.println(
                "[Metric] requestId: "
                        + recognizer.getLastRequestId()
                        + ", first package delay ms: "
                        + recognizer.getFirstPackageDelay()
                        + ", last package delay ms: "
                        + recognizer.getLastPackageDelay());
        System.exit(0);
    }
}
