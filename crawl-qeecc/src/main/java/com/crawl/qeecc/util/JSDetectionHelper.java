package com.crawl.qeecc.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaScript内容检测助手
 * 专门用于从HTML页面的JavaScript内容中提取音频URL
 */
public class JSDetectionHelper {
    
    /**
     * 从页面文档中检测音频URL
     */
    public static List<String> detectAudioUrlsFromJS(Document document) {
        List<String> audioUrls = new ArrayList<>();
        
        // 搜索所有的script标签
        Elements scripts = document.select("script");
        
        for (Element script : scripts) {
            String scriptContent = script.html();
            
            if (scriptContent != null && !scriptContent.trim().isEmpty()) {
                // 检测不同格式的音频URL
                audioUrls.addAll(extractAudioUrlsFromJS(scriptContent));
            }
        }
        
        // 也要检查页面源码中的内联事件处理器
        String pageHtml = document.html();
        audioUrls.addAll(extractAudioUrlsFromJS(pageHtml));
        
        return audioUrls;
    }
    
    /**
     * 从JavaScript内容中提取音频URL
     */
    private static List<String> extractAudioUrlsFromJS(String jsContent) {
        List<String> urls = new ArrayList<>();
        
        // 模式1: JSON对象中的音频URL
        Pattern jsonAudioPattern = Pattern.compile("\"?(?:audio|src|url|mp3|music|file|source)\"?\\s*[:=]\\s*\"([^\"]*\\.(?:mp3|wav|ogg|m4a|aac|flac))\"");
        Matcher jsonMatcher = jsonAudioPattern.matcher(jsContent);
        while (jsonMatcher.find()) {
            String url = jsonMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        // 模式2: 单引号包围的音频URL
        Pattern singleQuotePattern = Pattern.compile("'([^']*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))'");
        Matcher singleQuoteMatcher = singleQuotePattern.matcher(jsContent);
        while (singleQuoteMatcher.find()) {
            String url = singleQuoteMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        // 模式3: 双引号包围的音频URL
        Pattern doubleQuotePattern = Pattern.compile("\"([^\"]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))\"");
        Matcher doubleQuoteMatcher = doubleQuotePattern.matcher(jsContent);
        while (doubleQuoteMatcher.find()) {
            String url = doubleQuoteMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        // 模式4: 变量赋值形式的音频URL
        Pattern varAssignmentPattern = Pattern.compile("(?:var|let|const)\\s+\\w+\\s*=\\s*['\"]([^'\"]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
        Matcher varMatcher = varAssignmentPattern.matcher(jsContent);
        while (varMatcher.find()) {
            String url = varMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        // 模式5: 对象属性赋值形式的音频URL
        Pattern objPropertyPattern = Pattern.compile("\\.\\w+\\s*=\\s*['\"]([^'\"]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
        Matcher objMatcher = objPropertyPattern.matcher(jsContent);
        while (objMatcher.find()) {
            String url = objMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        // 模式6: 函数参数中的音频URL
        Pattern funcParamPattern = Pattern.compile("\\w+\\s*\\(\\s*['\"]([^'\"]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
        Matcher funcMatcher = funcParamPattern.matcher(jsContent);
        while (funcMatcher.find()) {
            String url = funcMatcher.group(1);
            if (isValidAudioUrl(url)) {
                urls.add(url);
            }
        }
        
        return urls;
    }
    
    /**
     * 验证URL是否为有效的音频URL
     */
    private static boolean isValidAudioUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        // 检查是否包含音频文件扩展名
        String lowerUrl = url.toLowerCase();
        boolean hasValidExtension = lowerUrl.contains(".mp3") || 
                                   lowerUrl.contains(".wav") || 
                                   lowerUrl.contains(".ogg") || 
                                   lowerUrl.contains(".m4a") ||
                                   lowerUrl.contains(".aac") ||
                                   lowerUrl.contains(".flac");
        
        // 确保不是CSS或其他非音频文件
        boolean isNotInvalid = !lowerUrl.contains(".css") && 
                              !lowerUrl.contains(".js") && 
                              !lowerUrl.contains(".png") &&
                              !lowerUrl.contains(".jpg") &&
                              !lowerUrl.contains(".jpeg") &&
                              !lowerUrl.contains(".gif") &&
                              !lowerUrl.contains(".svg");
        
        return hasValidExtension && isNotInvalid;
    }
}