package com.crawl.qeecc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * qeecc.com 站内搜索服务
 * 利用网站搜索框接口: /so/{keyword}.html
 * 利用网站播放接口: POST /js/play.php
 */
@Service
public class QeeccSearchService {

    @Value("${qeecc.site.base-url:http://www.qeecc.com}")
    private String baseUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /** 缓存的 Cookie，用于绕过反爬 */
    private String cachedCookie = "";
    private long cookieTimestamp = 0;
    private static final long COOKIE_TTL_MS = 3600_000; // Cookie 有效期 1 小时

    public QeeccSearchService() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ==================== 搜索歌曲 ====================

    /**
     * 站内搜索歌曲
     * 访问 /so/{keyword}.html 获取搜索结果列表
     */
    public List<SearchResult> searchSongs(String keyword) {
        List<SearchResult> results = new ArrayList<>();
        try {
            ensureCookie();

            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String searchUrl = baseUrl + "/so/" + encodedKeyword + ".html";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(searchUrl))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                    .header("Referer", baseUrl + "/")
                    .header("Cookie", cachedCookie)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 如果返回 403，刷新 Cookie 重试
            if (response.statusCode() == 403) {
                refreshCookie();
                request = HttpRequest.newBuilder()
                        .uri(URI.create(searchUrl))
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                        .header("Referer", baseUrl + "/")
                        .header("Cookie", cachedCookie)
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            if (response.statusCode() == 200) {
                Document doc = Jsoup.parse(response.body());
                Elements items = doc.select(".play_list ul li .name a");
                for (Element item : items) {
                    String href = item.attr("href");
                    String text = item.text().trim();
                    if (href != null && href.contains("/song/") && !text.isEmpty()) {
                        // 从 /song/c2R3eW4.html 提取歌曲 ID
                        String songId = extractSongId(href);
                        // 从 "林俊杰《江南》[MP3]" 提取歌手和歌名
                        String[] parsed = parseSongText(text);
                        results.add(new SearchResult(songId, parsed[0], parsed[1], href, text));
                    }
                }
                System.out.println("[Search] 搜索 '" + keyword + "' 找到 " + results.size() + " 首歌曲");
            } else {
                System.out.println("[Search] 搜索请求失败, HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("[Search] 搜索异常: " + e.getMessage());
        }
        return results;
    }

    // ==================== 获取播放链接 ====================

    /**
     * 通过播放 API 获取歌曲 MP3 直链
     * POST /js/play.php  id={songId}&type=music
     */
    public PlayResult getPlayUrl(String songId) {
        try {
            String playUrl = baseUrl + "/js/play.php";
            String body = "id=" + songId + "&type=music";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(playUrl))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                    .header("Referer", baseUrl + "/song/" + songId + ".html")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                if (json.has("url") && !json.get("url").asText().isEmpty()) {
                    String mp3Url = json.get("url").asText();
                    String title = json.has("title") ? json.get("title").asText() : "";
                    String pic = json.has("pic") ? json.get("pic").asText() : "";
                    int lkid = json.has("lkid") ? json.get("lkid").asInt() : 0;
                    System.out.println("[Play] 获取播放链接成功: " + title);
                    return new PlayResult(mp3Url, title, pic, lkid);
                } else {
                    System.out.println("[Play] 播放链接为空");
                }
            } else {
                System.out.println("[Play] 获取播放链接失败, HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("[Play] 获取播放链接异常: " + e.getMessage());
        }
        return null;
    }

    /**
     * 一站式搜索: 搜索歌曲 + 获取第一首的播放链接
     */
    public SearchWithPlayResult searchAndGetPlayUrl(String keyword) {
        List<SearchResult> searchResults = searchSongs(keyword);
        if (searchResults.isEmpty()) {
            return new SearchWithPlayResult(keyword, List.of(), null, false);
        }

        // 取第一首获取播放链接
        SearchResult first = searchResults.get(0);
        PlayResult playResult = getPlayUrl(first.songId());

        return new SearchWithPlayResult(keyword, searchResults, playResult, true);
    }

    // ==================== Cookie 管理 ====================

    private void ensureCookie() {
        long now = System.currentTimeMillis();
        if (cachedCookie.isEmpty() || (now - cookieTimestamp) > COOKIE_TTL_MS) {
            refreshCookie();
        }
    }

    private void refreshCookie() {
        try {
            // 先访问任意页面获取 Cookie
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/"))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 从 Set-Cookie 头提取 Cookie
            List<String> cookies = response.headers().allValues("Set-Cookie");
            StringBuilder sb = new StringBuilder();
            for (String cookie : cookies) {
                String[] parts = cookie.split(";");
                if (parts.length > 0) {
                    if (!sb.isEmpty()) sb.append("; ");
                    sb.append(parts[0].trim());
                }
            }
            cachedCookie = sb.toString();
            cookieTimestamp = System.currentTimeMillis();
            System.out.println("[Cookie] 刷新成功: " + cachedCookie);
        } catch (Exception e) {
            System.out.println("[Cookie] 刷新失败: " + e.getMessage());
        }
    }

    // ==================== 辅助方法 ====================

    private String extractSongId(String href) {
        // /song/c2R3eW4.html -> c2R3eW4
        int start = href.lastIndexOf("/song/");
        if (start >= 0) {
            start += "/song/".length();
            int end = href.indexOf(".html", start);
            if (end > start) {
                return href.substring(start, end);
            }
        }
        return href;
    }

    /**
     * 解析歌曲文本: "林俊杰《江南》[MP3]" -> ["林俊杰", "江南"]
     */
    private String[] parseSongText(String text) {
        String singer = "";
        String songName = "";
        // 去掉 [MP3] 后缀
        text = text.replaceAll("\\[.*?\\]", "").trim();
        // 按《》分割
        int left = text.indexOf("《");
        int right = text.indexOf("》");
        if (left >= 0 && right > left) {
            singer = text.substring(0, left).trim();
            songName = text.substring(left + 1, right).trim();
        } else {
            songName = text;
        }
        return new String[]{singer, songName};
    }

    // ==================== 数据结构 ====================

    /**
     * 搜索结果
     */
    public record SearchResult(
            String songId,      // 歌曲ID, 如 c2R3eW4
            String singer,      // 歌手名
            String songName,    // 歌曲名
            String href,        // 详情页路径
            String fullText     // 完整文本
    ) {}

    /**
     * 播放信息
     */
    public record PlayResult(
            String mp3Url,      // MP3 直链
            String title,       // 歌曲标题
            String pic,         // 封面图
            int lkid            // 歌词/下载 ID
    ) {}

    /**
     * 搜索 + 播放 一站式结果
     */
    public record SearchWithPlayResult(
            String keyword,
            List<SearchResult> searchResults,
            PlayResult playResult,
            boolean found
    ) {}
}
