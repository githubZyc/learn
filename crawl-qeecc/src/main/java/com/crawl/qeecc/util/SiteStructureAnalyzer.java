package com.crawl.qeecc.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 网站结构分析器
 * 用于探测和分析网站的布局结构，特别关注音频相关内容
 */
@Component
public class SiteStructureAnalyzer {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * 分析网站结构并查找音频相关元素
     * @param url 要分析的网址
     * @return 网站结构分析结果
     */
    public SiteAnalysisResult analyzeSiteStructure(String url) {
        SiteAnalysisResult result = new SiteAnalysisResult();
        result.setUrl(url);

        try {
            // 尝试获取网页内容
            String htmlContent = HttpUtil.createGet(url)
                    .header("User-Agent", DEFAULT_USER_AGENT)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .timeout(10000)
                    .execute()
                    .body();

            // 检查是否包含重定向脚本
            if (htmlContent.contains("window.location.href")) {
                List<String> redirectUrls = ReUtil.findAll("window\\.location\\.href\\s*=\\s*[\"']([^\"']+)[\"']", htmlContent, 1);
                if (!redirectUrls.isEmpty()) {
                    result.setRedirectUrls(redirectUrls);
                }
            }

            // 使用Jsoup解析HTML
            Document document = Jsoup.parse(htmlContent, url);

            // 分析标题
            result.setTitle(document.title());

            // 分析导航栏和菜单
            analyzeNavigation(document, result);

            // 分析音频元素
            analyzeAudioElements(document, result);

            // 分析页面中的所有链接
            analyzeAllLinks(document, result);

        } catch (Exception e) {
            result.setError(e.getMessage());
        }

        return result;
    }

    /**
     * 分析导航栏和菜单
     */
    private void analyzeNavigation(Document document, SiteAnalysisResult result) {
        // 查找常见的导航元素
        Elements navElements = document.select("nav, .nav, .navbar, .menu, .navigation, ul li a");
        
        for (Element navElement : navElements) {
            String text = navElement.text().trim();
            String href = navElement.absUrl("href");
            
            if (StrUtil.isNotBlank(text)) {
                result.getNavigationItems().put(text, href);
                
                // 检查是否包含与"DJ舞曲"相关的导航项
                if (text.toLowerCase().contains("dj") || text.toLowerCase().contains("舞曲")) {
                    result.getDanceMusicLinks().add(new NavigationLink(text, href));
                }
            }
        }
        
        // 查找特定的选择器模式
        Elements djRelated = document.select("a:contains(DJ), a:contains(舞曲), a:contains(dj), div:contains(DJ), div:contains(舞曲)");
        for (Element element : djRelated) {
            String text = element.text().trim();
            String href = element.absUrl("href");
            if (StrUtil.isNotBlank(text) && StrUtil.isNotBlank(href)) {
                result.getDanceMusicLinks().add(new NavigationLink(text, href));
            }
        }
    }

    /**
     * 分析音频相关元素
     */
    private void analyzeAudioElements(Document document, SiteAnalysisResult result) {
        // 查找所有的audio标签
        Elements audioTags = document.select("audio");
        for (Element audioTag : audioTags) {
            String src = audioTag.attr("src");
            String title = audioTag.attr("title");
            String alt = audioTag.attr("alt");
            
            AudioElement audioElement = new AudioElement();
            audioElement.setType("audio_tag");
            audioElement.setSrc(src);
            audioElement.setTitle(StrUtil.blankToDefault(title, alt));
            audioElement.setHtml(audioTag.toString());
            
            result.getAudioElements().add(audioElement);
        }

        // 查找source标签（通常是audio标签内的）
        Elements sourceTags = document.select("source[type*=audio], source[src$=.mp3], source[src$=.wav], source[src$=.ogg], source[src$=.m4a]");
        for (Element sourceTag : sourceTags) {
            String src = sourceTag.attr("src");
            Element parent = sourceTag.parent();
            String title = parent != null ? parent.text().trim() : "";
            
            AudioElement audioElement = new AudioElement();
            audioElement.setType("source_tag");
            audioElement.setSrc(src);
            audioElement.setTitle(title);
            audioElement.setHtml(sourceTag.toString());
            
            result.getAudioElements().add(audioElement);
        }

        // 查找可能包含音频链接的a标签
        Elements audioLinks = document.select("a[href$=.mp3], a[href$=.wav], a[href$=.ogg], a[href$=.m4a]");
        for (Element link : audioLinks) {
            String href = link.attr("href");
            String text = link.text().trim();
            
            AudioElement audioElement = new AudioElement();
            audioElement.setType("link");
            audioElement.setSrc(href);
            audioElement.setTitle(text);
            audioElement.setHtml(link.toString());
            
            result.getAudioElements().add(audioElement);
        }

        // 使用正则表达式查找可能的音频URL
        Set<String> potentialAudioUrls = findPotentialAudioUrls(document.html());
        for (String url : potentialAudioUrls) {
            AudioElement audioElement = new AudioElement();
            audioElement.setType("potential_url");
            audioElement.setSrc(url);
            audioElement.setTitle("Potential Audio URL");
            
            result.getAudioElements().add(audioElement);
        }
    }

    /**
     * 从HTML内容中查找潜在的音频URL
     */
    private Set<String> findPotentialAudioUrls(String html) {
        Set<String> urls = new HashSet<>();
        
        // 匹配各种音频文件扩展名
        Pattern audioPattern = Pattern.compile("(https?://[\\w\\d\\-_@./?&=#]+\\.(mp3|wav|ogg|m4a|aac|flac))", Pattern.CASE_INSENSITIVE);
        urls.addAll(ReUtil.findAllGroup1(audioPattern, html));
        
        // 匹配引号内的音频文件路径
        Pattern quotedAudioPattern = Pattern.compile("[\"'](.*?\\.(mp3|wav|ogg|m4a|aac|flac))[\"']", Pattern.CASE_INSENSITIVE);
        List<String> matches = ReUtil.findAllGroup1(quotedAudioPattern, html);
        for (String match : matches) {
            if (!match.startsWith("http")) {
                // 尝试构造完整URL（这里需要知道基础URL才能正确构造）
                urls.add(match);
            } else {
                urls.add(match);
            }
        }
        
        return urls;
    }

    /**
     * 分析页面中的所有链接
     */
    private void analyzeAllLinks(Document document, SiteAnalysisResult result) {
        Elements allLinks = document.select("a[href]");
        for (Element link : allLinks) {
            String href = link.attr("href");
            String text = link.text().trim();
            String absUrl = link.absUrl("href"); // 绝对URL
            
            if (StrUtil.isNotBlank(absUrl)) {
                result.getAllLinks().add(new PageLink(text, href, absUrl));
            }
        }
    }

    /**
     * 网站分析结果类
     */
    public static class SiteAnalysisResult {
        private String url;
        private String title;
        private Map<String, String> navigationItems = new LinkedHashMap<>();
        private List<AudioElement> audioElements = new ArrayList<>();
        private List<PageLink> allLinks = new ArrayList<>();
        private List<NavigationLink> danceMusicLinks = new ArrayList<>();
        private List<String> redirectUrls = new ArrayList<>();
        private String error;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Map<String, String> getNavigationItems() { return navigationItems; }
        public void setNavigationItems(Map<String, String> navigationItems) { this.navigationItems = navigationItems; }

        public List<AudioElement> getAudioElements() { return audioElements; }
        public void setAudioElements(List<AudioElement> audioElements) { this.audioElements = audioElements; }

        public List<PageLink> getAllLinks() { return allLinks; }
        public void setAllLinks(List<PageLink> allLinks) { this.allLinks = allLinks; }

        public List<NavigationLink> getDanceMusicLinks() { return danceMusicLinks; }
        public void setDanceMusicLinks(List<NavigationLink> danceMusicLinks) { this.danceMusicLinks = danceMusicLinks; }

        public List<String> getRedirectUrls() { return redirectUrls; }
        public void setRedirectUrls(List<String> redirectUrls) { this.redirectUrls = redirectUrls; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        @Override
        public String toString() {
            return "SiteAnalysisResult{" +
                    "url='" + url + '\'' +
                    ", title='" + title + '\'' +
                    ", navigationItemCount=" + navigationItems.size() +
                    ", audioElementsCount=" + audioElements.size() +
                    ", allLinksCount=" + allLinks.size() +
                    ", danceMusicLinksCount=" + danceMusicLinks.size() +
                    ", redirectUrlsCount=" + redirectUrls.size() +
                    ", error='" + error + '\'' +
                    '}';
        }
    }

    /**
     * 音频元素类
     */
    public static class AudioElement {
        private String type;      // 元素类型: audio_tag, source_tag, link, potential_url
        private String src;       // 音频源URL
        private String title;     // 标题或描述
        private String html;      // 原始HTML

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getSrc() { return src; }
        public void setSrc(String src) { this.src = src; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getHtml() { return html; }
        public void setHtml(String html) { this.html = html; }

        @Override
        public String toString() {
            return "AudioElement{" +
                    "type='" + type + '\'' +
                    ", src='" + src + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    /**
     * 导航链接类
     */
    public static class NavigationLink {
        private String text;
        private String url;

        public NavigationLink(String text, String url) {
            this.text = text;
            this.url = url;
        }

        // Getters
        public String getText() { return text; }
        public String getUrl() { return url; }

        @Override
        public String toString() {
            return "NavigationLink{" +
                    "text='" + text + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    /**
     * 页面链接类
     */
    public static class PageLink {
        private String text;
        private String relativeUrl;
        private String absoluteUrl;

        public PageLink(String text, String relativeUrl, String absoluteUrl) {
            this.text = text;
            this.relativeUrl = relativeUrl;
            this.absoluteUrl = absoluteUrl;
        }

        // Getters
        public String getText() { return text; }
        public String getRelativeUrl() { return relativeUrl; }
        public String getAbsoluteUrl() { return absoluteUrl; }

        @Override
        public String toString() {
            return "PageLink{" +
                    "text='" + text + '\'' +
                    ", relativeUrl='" + relativeUrl + '\'' +
                    ", absoluteUrl='" + absoluteUrl + '\'' +
                    '}';
        }
    }
}