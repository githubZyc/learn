package com.crawl.qeecc.service;

import com.crawl.qeecc.util.SiteStructureAnalyzer;
import com.crawl.qeecc.util.WebPageFetcher;
import com.crawl.qeecc.util.JSDetectionHelper;
import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DJ舞曲爬虫服务
 * 专门用于定位和爬取DJ舞曲相关内容
 */
@Service
public class DanceMusicCrawler {

    @Autowired
    private WebPageFetcher webPageFetcher;

    @Autowired
    private SiteStructureAnalyzer siteStructureAnalyzer;

    @Value("${qeecc.site.url}")
    private String targetSiteUrl;
    
    /**
     * 查找DJ舞曲相关的链接
     */
    public List<DanceMusicCategory> findDanceMusicCategories() {
        List<DanceMusicCategory> categories = new ArrayList<>();

        try {
            // 首先获取主页内容
            WebPageFetcher.FetchResult fetchResult = webPageFetcher.fetchPageContent(targetSiteUrl);
            
            if (!fetchResult.isSuccess()) {
                return categories;
            }

            // 如果发生了重定向，使用重定向后的URL
            String actualUrl = fetchResult.isRedirected() ? fetchResult.getRedirectUrl() : targetSiteUrl;
            Document document = fetchResult.getDocument();

            // 在文档中查找DJ舞曲相关链接
            // 方式1: 通过文本内容查找
            Elements djLinksByText = document.select("a:containsOwn(DJ), a:matchesOwn((?i)dj), a:containsOwn(舞曲), a:matchesOwn((?i)舞曲)");
            for (Element link : djLinksByText) {
                String href = link.absUrl("href");
                String text = link.text().trim();
                
                if (isDanceMusicRelated(text)) {
                    DanceMusicCategory category = new DanceMusicCategory();
                    category.setName(text);
                    category.setUrl(href);
                    category.setSourceType("text_match");
                    categories.add(category);
                }
            }

            // 方式2: 通过CSS类名或ID查找
            Elements djLinksByClass = document.select("a[class*='dj'], a[id*='dj'], a[class*='dance'], a[id*='dance'], " +
                    "a[class*='舞曲'], a[id*='舞曲'], div[class*='dj'] a, div[id*='dj'] a, " +
                    "li[class*='dj'] a, li[id*='dj'] a");
            for (Element link : djLinksByClass) {
                String href = link.absUrl("href");
                String text = link.text().trim();
                
                if (StrUtil.isNotBlank(href) && (isDanceMusicRelated(text) || isDanceMusicRelated(href))) {
                    DanceMusicCategory category = new DanceMusicCategory();
                    category.setName(StrUtil.blankToDefault(text, href));
                    category.setUrl(href);
                    category.setSourceType("class_or_id_match");
                    categories.add(category);
                }
            }

            // 方式3: 分析导航结构
            SiteStructureAnalyzer.SiteAnalysisResult analysisResult = siteStructureAnalyzer.analyzeSiteStructure(actualUrl);
            for (SiteStructureAnalyzer.NavigationLink navLink : analysisResult.getDanceMusicLinks()) {
                DanceMusicCategory category = new DanceMusicCategory();
                category.setName(navLink.getText());
                category.setUrl(navLink.getUrl());
                category.setSourceType("navigation_analysis");
                categories.add(category);
            }

            // 过滤重复和无效的分类
            categories.removeIf(category -> StrUtil.isBlank(category.getUrl()));
            
            // 使用名字作为键来去重
            List<DanceMusicCategory> uniqueCategories = new ArrayList<>();
            for (DanceMusicCategory category : categories) {
                boolean exists = false;
                for (DanceMusicCategory uniqueCategory : uniqueCategories) {
                    if (uniqueCategory.getName().equals(category.getName())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    uniqueCategories.add(category);
                }
            }
            
            categories = uniqueCategories;
            
            for (DanceMusicCategory category : categories) {
                System.out.println("DJ舞曲分类: " + category.getName() + " -> " + category.getUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    /**
     * 检查文本是否与舞蹈音乐相关
     */
    private boolean isDanceMusicRelated(String text) {
        if (text == null) return false;
        
        String lowerText = text.toLowerCase();
        return lowerText.contains("dj") || 
               lowerText.contains("舞曲") || 
               lowerText.contains("dance") || 
               lowerText.contains("music") ||
               lowerText.contains("电子") ||
               lowerText.contains("electro") ||
               lowerText.contains("house") ||
               lowerText.contains("techno") ||
               lowerText.contains("trance");
    }

    /**
     * 从指定的DJ舞曲分类页面获取音频链接
     */
    public List<AudioTrack> getAudioTracksFromCategory(String categoryUrl) {
        List<AudioTrack> tracks = new ArrayList<>();

        try {
            WebPageFetcher.FetchResult fetchResult = webPageFetcher.fetchPageContent(categoryUrl);
            
            if (!fetchResult.isSuccess()) {
                return tracks;
            }

            Document document = fetchResult.getDocument();

            // 查找页面中的音频元素
            // 1. 查找audio标签
            Elements audioTags = document.select("audio[src], audio > source[src]");
            for (Element audioTag : audioTags) {
                String src = audioTag.attr("src");
                if (src.isEmpty()) {
                    src = audioTag.selectFirst("source") != null ? audioTag.selectFirst("source").attr("src") : "";
                }
                
                if (!src.isEmpty()) {
                    AudioTrack track = extractTrackInfo(audioTag, src, categoryUrl);
                    if (track != null) {
                        tracks.add(track);
                    }
                }
            }

            // 2. 查找可能的音频链接
            Elements potentialAudioLinks = document.select("a[href$=.mp3], a[href$=.wav], a[href$=.ogg], a[href$=.m4a]");
            for (Element link : potentialAudioLinks) {
                String href = link.absUrl("href");
                String title = link.text().trim();
                
                AudioTrack track = new AudioTrack();
                track.setTitle(StrUtil.blankToDefault(title, "Unknown Track"));
                track.setAudioUrl(href);
                track.setPageUrl(categoryUrl);
                track.setSourceType("direct_audio_link");
                
                tracks.add(track);
            }

            // 3. 查找可能嵌入在data属性中的音频URL
            Elements dataAudioElements = document.select("[data-audio], [data-src], [data-url], [data-file], [data-mp3], [data-music]");
            for (Element element : dataAudioElements) {
                String dataAudio = element.attr("data-audio");
                if (dataAudio.isEmpty()) dataAudio = element.attr("data-src");
                if (dataAudio.isEmpty()) dataAudio = element.attr("data-url");
                if (dataAudio.isEmpty()) dataAudio = element.attr("data-file");
                if (dataAudio.isEmpty()) dataAudio = element.attr("data-mp3");
                if (dataAudio.isEmpty()) dataAudio = element.attr("data-music");
                
                if (!dataAudio.isEmpty() && isAudioUrl(dataAudio)) {
                    AudioTrack track = new AudioTrack();
                    track.setTitle(element.text().trim());
                    track.setAudioUrl(dataAudio);
                    track.setPageUrl(categoryUrl);
                    track.setSourceType("data_attribute");
                    
                    tracks.add(track);
                }
            }

            // 4. 查找可能在script标签中的音频URL
            Elements scriptElements = document.select("script");
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                
                // 查找常见的音频URL模式 - 多种格式
                // 格式1: var audioUrl = "xxx.mp3"
                Pattern audioPattern1 = Pattern.compile("(?:src|url|mp3|music|audio)\\s*[=:,]\\s*['\"]([^'\"\\s]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
                Matcher matcher1 = audioPattern1.matcher(scriptContent);
                
                while (matcher1.find()) {
                    String matchedUrl = matcher1.group(1);
                    if (isAudioUrl(matchedUrl)) {
                        // 如果是相对路径，转换为绝对路径
                        if (!matchedUrl.startsWith("http")) {
                            matchedUrl = resolveRelativeUrl(categoryUrl, matchedUrl);
                        }
                        
                        AudioTrack track = new AudioTrack();
                        track.setTitle("Audio from script");
                        track.setAudioUrl(matchedUrl);
                        track.setPageUrl(categoryUrl);
                        track.setSourceType("script_content");
                        
                        tracks.add(track);
                    }
                }
                
                // 格式2: data-audio="xxx.mp3"
                Pattern audioPattern2 = Pattern.compile("data-(?:audio|src|url|file)\\s*=\\s*['\"]([^'\"\\s]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
                Matcher matcher2 = audioPattern2.matcher(scriptContent);
                
                while (matcher2.find()) {
                    String matchedUrl = matcher2.group(1);
                    if (isAudioUrl(matchedUrl)) {
                        // 如果是相对路径，转换为绝对路径
                        if (!matchedUrl.startsWith("http")) {
                            matchedUrl = resolveRelativeUrl(categoryUrl, matchedUrl);
                        }
                        
                        AudioTrack track = new AudioTrack();
                        track.setTitle("Audio from script data attribute");
                        track.setAudioUrl(matchedUrl);
                        track.setPageUrl(categoryUrl);
                        track.setSourceType("script_data_attribute");
                        
                        tracks.add(track);
                    }
                }
                
                // 格式3: JSON格式的音频配置
                Pattern jsonPattern = Pattern.compile("\\{[^}]*['\"]*(?:src|url|mp3|music|audio)[^}]*['\"]*[:=][^}]*['\"]([^'\"\\s]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
                Matcher jsonMatcher = jsonPattern.matcher(scriptContent);
                
                while (jsonMatcher.find()) {
                    String matchedUrl = jsonMatcher.group(1);
                    if (isAudioUrl(matchedUrl)) {
                        // 如果是相对路径，转换为绝对路径
                        if (!matchedUrl.startsWith("http")) {
                            matchedUrl = resolveRelativeUrl(categoryUrl, matchedUrl);
                        }
                        
                        AudioTrack track = new AudioTrack();
                        track.setTitle("Audio from JSON in script");
                        track.setAudioUrl(matchedUrl);
                        track.setPageUrl(categoryUrl);
                        track.setSourceType("script_json");
                        
                        tracks.add(track);
                    }
                }
            }

            // 5. 查找可能在JSON LD中的音频信息
            Elements jsonLdElements = document.select("script[type='application/ld+json']");
            for (Element jsonLd : jsonLdElements) {
                String jsonContent = jsonLd.html();
                if (jsonContent.contains("audio") || jsonContent.contains("AudioObject")) {
                    // 简单的正则匹配音频URL
                    Pattern jsonAudioPattern = Pattern.compile("(?:audio|contentUrl|url)\\s*:\\s*['\"]([^'\"\\s]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
                    Matcher matcher = jsonAudioPattern.matcher(jsonContent);
                    
                    while (matcher.find()) {
                        String matchedUrl = matcher.group(1);
                        if (isAudioUrl(matchedUrl)) {
                            // 如果是相对路径，转换为绝对路径
                            if (!matchedUrl.startsWith("http")) {
                                matchedUrl = resolveRelativeUrl(categoryUrl, matchedUrl);
                            }
                            
                            AudioTrack track = new AudioTrack();
                            track.setTitle("Audio from JSON-LD");
                            track.setAudioUrl(matchedUrl);
                            track.setPageUrl(categoryUrl);
                            track.setSourceType("json_ld");
                            
                            tracks.add(track);
                        }
                    }
                }
            }
            
            // 6. 查找可能在JavaScript变量中的音频URL
            Elements allScriptElements = document.select("script");
            for (Element script : allScriptElements) {
                String scriptContent = script.html();
                
                // 检查是否包含音频相关的JavaScript函数调用
                Pattern jsAudioPattern = Pattern.compile("(?:play|load|setSource|setAudio|addTrack)(?:\\(|\\s*=\\s*)[^}]*['\"]([^'\"\\s]*(?:\\.mp3|\\.wav|\\.ogg|\\.m4a|\\.aac|\\.flac))['\"]");
                Matcher jsMatcher = jsAudioPattern.matcher(scriptContent);
                
                while (jsMatcher.find()) {
                    String matchedUrl = jsMatcher.group(1);
                    if (isAudioUrl(matchedUrl)) {
                        // 如果是相对路径，转换为绝对路径
                        if (!matchedUrl.startsWith("http")) {
                            matchedUrl = resolveRelativeUrl(categoryUrl, matchedUrl);
                        }
                        
                        AudioTrack track = new AudioTrack();
                        track.setTitle("Audio from JS function");
                        track.setAudioUrl(matchedUrl);
                        track.setPageUrl(categoryUrl);
                        track.setSourceType("javascript_function");
                        
                        tracks.add(track);
                    }
                }
            }
            
            // 7. 使用JS检测助手深度扫描JavaScript内容
            List<String> jsDetectedUrls = JSDetectionHelper.detectAudioUrlsFromJS(document);
            for (String audioUrl : jsDetectedUrls) {
                // 如果是相对路径，转换为绝对路径
                if (!audioUrl.startsWith("http")) {
                    audioUrl = resolveRelativeUrl(categoryUrl, audioUrl);
                }
                
                AudioTrack track = new AudioTrack();
                track.setTitle("Audio from JS detection");
                track.setAudioUrl(audioUrl);
                track.setPageUrl(categoryUrl);
                track.setSourceType("js_detection_helper");
                
                tracks.add(track);
            }

            // 过滤掉重复和无效的音频轨道
            tracks.removeIf(track -> StrUtil.isBlank(track.getAudioUrl()));
            
            // 使用音频URL作为键来去重
            List<AudioTrack> uniqueTracks = new ArrayList<>();
            for (AudioTrack track : tracks) {
                boolean exists = false;
                for (AudioTrack uniqueTrack : uniqueTracks) {
                    if (uniqueTrack.getAudioUrl().equals(track.getAudioUrl())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    uniqueTracks.add(track);
                }
            }
            
            tracks = uniqueTracks;
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }

    /**
     * 提取音频轨道信息
     */
    private AudioTrack extractTrackInfo(Element audioElement, String audioUrl, String pageUrl) {
        if (!isAudioUrl(audioUrl)) {
            return null;
        }

        AudioTrack track = new AudioTrack();
        track.setAudioUrl(audioUrl);
        track.setPageUrl(pageUrl);

        // 尝试从父元素或附近元素获取标题信息
        String title = audioElement.attr("title");
        if (title.isEmpty()) {
            title = audioElement.attr("alt");
        }
        if (title.isEmpty()) {
            Element parent = audioElement.parent();
            if (parent != null) {
                title = parent.text().trim();
            }
        }
        if (title.isEmpty()) {
            // 查找附近的标题元素
            Element sibling = audioElement.previousElementSibling();
            if (sibling != null) {
                title = sibling.text().trim();
            }
        }
        
        track.setTitle(StrUtil.blankToDefault(title, "Unknown Track"));
        track.setSourceType("audio_element");

        return track;
    }
    
    /**
     * 解析相对URL为绝对URL
     */
    private String resolveRelativeUrl(String baseUrl, String relativeUrl) {
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            java.net.URL resolved = new java.net.URL(base, relativeUrl);
            return resolved.toString();
        } catch (Exception e) {
            // 如果解析失败，返回原始URL
            if (relativeUrl.startsWith("/")) {
                // 相对于根路径
                String baseDomain = baseUrl.replace(baseUrl.substring(baseUrl.indexOf('/', 8)), "");
                return baseDomain + relativeUrl;
            } else {
                // 相对于当前路径
                String basePath = baseUrl.substring(0, baseUrl.lastIndexOf('/') + 1);
                return basePath + relativeUrl;
            }
        }
    }

    /**
     * 检查URL是否为音频文件
     */
    private boolean isAudioUrl(String url) {
        if (url == null) return false;
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".mp3") || 
               lowerUrl.endsWith(".wav") || 
               lowerUrl.endsWith(".ogg") || 
               lowerUrl.endsWith(".m4a") ||
               lowerUrl.endsWith(".aac") ||
               lowerUrl.endsWith(".flac");
    }

    /**
     * DJ舞曲分类信息
     */
    public static class DanceMusicCategory {
        private String name;          // 分类名称
        private String url;           // 分类页面URL
        private String sourceType;    // 来源类型

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }

        @Override
        public String toString() {
            return "DanceMusicCategory{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", sourceType='" + sourceType + '\'' +
                    '}';
        }
    }

    /**
     * 音频轨道信息
     */
    public static class AudioTrack {
        private String title;         // 音轨标题
        private String audioUrl;      // 音频文件URL
        private String pageUrl;       // 所在页面URL
        private String sourceType;    // 来源类型

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

        public String getPageUrl() { return pageUrl; }
        public void setPageUrl(String pageUrl) { this.pageUrl = pageUrl; }

        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }

        @Override
        public String toString() {
            return "AudioTrack{" +
                    "title='" + title + '\'' +
                    ", audioUrl='" + audioUrl + '\'' +
                    ", pageUrl='" + pageUrl + '\'' +
                    ", sourceType='" + sourceType + '\'' +
                    '}';
        }
    }
}