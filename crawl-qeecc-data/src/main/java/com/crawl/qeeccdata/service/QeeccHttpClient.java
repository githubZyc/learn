package com.crawl.qeeccdata.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * qeecc.com HTTP 客户端
 * 封装 Cookie 管理、页面获取、搜索、播放 API 调用
 */
@Service
public class QeeccHttpClient {

    @Value("${qeecc.site.base-url:http://www.qeecc.com}")
    private String baseUrl;

    @Value("${qeecc.crawl.request-interval-ms:2000}")
    private long requestIntervalMs;

    @Value("${qeecc.crawl.cookie-refresh-minutes:50}")
    private long cookieRefreshMinutes;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /** 缓存 Cookie */
    private String cachedCookie = "";
    private long cookieTimestamp = 0;

    /** 完整浏览器 UA, 模拟 Chrome 绕过 CDN 防盗链 */
    private static final String BROWSER_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    public QeeccHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ==================== Cookie 管理 ====================

    /**
     * 确保 Cookie 有效, 过期则自动刷新
     */
    private synchronized void ensureCookie() {
        long now = System.currentTimeMillis();
        if (cachedCookie.isEmpty() || (now - cookieTimestamp) > cookieRefreshMinutes * 60_000) {
            refreshCookie();
        }
    }

    /**
     * 刷新 Cookie: 先访问首页获取 Set-Cookie
     */
    private void refreshCookie() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/"))
                    .header("User-Agent", BROWSER_UA)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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
            System.out.println("[HttpClient] Cookie 刷新成功");
        } catch (Exception e) {
            System.out.println("[HttpClient] Cookie 刷新失败: " + e.getMessage());
        }
    }

    // ==================== 页面获取 ====================

    /**
     * 获取页面 Document (带 Cookie + 403 自动重试)
     */
    public Document fetchPage(String url) {
        ensureCookie();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", BROWSER_UA)
                    .header("Referer", baseUrl + "/")
                    .header("Cookie", cachedCookie)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 403 自动刷新 Cookie 重试
            if (response.statusCode() == 403) {
                System.out.println("[HttpClient] 403, 刷新 Cookie 重试: " + url);
                refreshCookie();
                request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", BROWSER_UA)
                        .header("Referer", baseUrl + "/")
                        .header("Cookie", cachedCookie)
                        .GET()
                        .build();
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            }

            if (response.statusCode() == 200) {
                return Jsoup.parse(response.body());
            } else {
                System.out.println("[HttpClient] 请求失败 HTTP " + response.statusCode() + ": " + url);
            }
        } catch (Exception e) {
            System.out.println("[HttpClient] 请求异常: " + url + " → " + e.getMessage());
        }
        return null;
    }

    // ==================== Play API ====================

    /**
     * 调用播放 API 获取 MP3 直链
     * POST /js/play.php  id={songId}&type=music
     */
    public PlayResult getPlayUrl(String songId) {
        try {
            String playUrl = baseUrl + "/js/play.php";
            String body = "id=" + songId + "&type=music";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(playUrl))
                    .header("User-Agent", BROWSER_UA)
                    .header("Referer", baseUrl + "/song/" + songId + ".html")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                if (json.has("url") && !json.get("url").asText().isEmpty()) {
                    return new PlayResult(
                            json.get("url").asText(),
                            json.has("title") ? json.get("title").asText() : "",
                            json.has("pic") ? json.get("pic").asText() : "",
                            json.has("lkid") ? json.get("lkid").asInt() : 0
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("[HttpClient] Play API 异常: " + songId + " → " + e.getMessage());
        }
        return null;
    }

    // ==================== 文件下载 ====================

    /**
     * 下载文件到本地 (流式写入, 模拟浏览器请求头绕过 CDN 防盗链)
     */
    public boolean downloadFile(String fileUrl, java.nio.file.Path targetPath, String referer) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .header("User-Agent", BROWSER_UA)
                    .header("Referer", baseUrl + "/")
                    .header("Accept", "*/*")
                    .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .header("Range", "bytes=0-")
                    .GET()
                    .build();

            // 先下载到 .tmp 文件
            java.nio.file.Path tempFile = java.nio.file.Path.of(targetPath + ".tmp");
            HttpResponse<java.nio.file.Path> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofFile(tempFile));

            // 206(Partial Content) 或 200 都算成功
            int status = response.statusCode();
            if (status == 200 || status == 206) {
                java.nio.file.Files.move(tempFile, targetPath,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                return true;
            } else {
                java.nio.file.Files.deleteIfExists(tempFile);
                System.out.println("[HttpClient] 下载失败 HTTP " + status + ": " + fileUrl);
            }
        } catch (Exception e) {
            System.out.println("[HttpClient] 下载异常: " + fileUrl + " → " + e.getMessage());
        }
        return false;
    }


    // ==================== 速率控制 ====================

    /**
     * 请求间隔控制, 避免触发反爬
     */
    public void rateLimit() {
        try {
            Thread.sleep(requestIntervalMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 从 /song/c2R3eW4.html 提取歌曲 ID
     */
    public String extractSongId(String href) {
        int start = href.lastIndexOf("/song/");
        if (start >= 0) {
            start += "/song/".length();
            int end = href.indexOf(".html", start);
            if (end > start) return href.substring(start, end);
        }
        return href;
    }

    /**
     * 解析歌曲文本: "林俊杰《江南》[MP3]" → ["林俊杰", "江南"]
     */
    public String[] parseSongText(String text) {
        String singer = "";
        String songName = "";
        text = text.replaceAll("\\[.*?\\]", "").trim();
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

    public String getBaseUrl() {
        return baseUrl;
    }

    // ==================== 数据结构 ====================

    public record PlayResult(String mp3Url, String title, String pic, int lkid) {}
}
