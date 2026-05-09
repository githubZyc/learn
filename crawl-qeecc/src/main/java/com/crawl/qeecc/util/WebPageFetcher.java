package com.crawl.qeecc.util;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 网页内容获取器
 * 处理各种网页获取场景，包括重定向、JavaScript渲染等
 */
@Component
public class WebPageFetcher {

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    
    /**
     * 获取网页内容，处理重定向和基本的JavaScript情况
     * @param url 目标URL
     * @return 网页内容获取结果
     */
    public FetchResult fetchPageContent(String url) {
        FetchResult result = new FetchResult();
        result.setOriginalUrl(url);

        try {
            // 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", DEFAULT_USER_AGENT);
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Connection", "keep-alive");
            headers.put("Upgrade-Insecure-Requests", "1");

            // 发起请求
            HttpRequest request = HttpRequest.get(url)
                    .timeout(15000);
            
            // 添加请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
            
            HttpResponse response = request.execute();
            String responseContent = response.body();

            result.setRawContent(responseContent);
            result.setSuccess(true);

            // 检测是否包含JavaScript重定向
            detectAndHandleRedirect(responseContent, url, result);

            // 解析HTML文档
            Document document = Jsoup.parse(responseContent, url);
            document.outputSettings().charset(java.nio.charset.Charset.forName("UTF-8"));
            result.setDocument(document);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    /**
     * 检测并处理JavaScript重定向
     */
    private void detectAndHandleRedirect(String content, String originalUrl, FetchResult result) {
        // 检测各种JavaScript重定向模式
        // 1. window.location.href 模式
        String locationHrefPattern = "(?i)window\\.location\\.href\\s*=\\s*[\"']([^\"']+)[\"']";
        if (ReUtil.contains(locationHrefPattern, content)) {
            String redirectUrl = ReUtil.get(locationHrefPattern, content, 1);
            if (StrUtil.isNotBlank(redirectUrl)) {
                result.setRedirectType("js_window_location_href");
                result.setRedirectUrl(resolveUrl(originalUrl, redirectUrl));
            }
        }

        // 2. window.location.replace 模式
        String locationReplacePattern = "(?i)window\\.location\\.replace\\s*\\(\\s*[\"']([^\"']+)[\"']\\s*\\)";
        if (ReUtil.contains(locationReplacePattern, content)) {
            String redirectUrl = ReUtil.get(locationReplacePattern, content, 1);
            if (StrUtil.isNotBlank(redirectUrl)) {
                result.setRedirectType("js_window_location_replace");
                result.setRedirectUrl(resolveUrl(originalUrl, redirectUrl));
            }
        }

        // 3. window.location.assign 模式
        String locationAssignPattern = "(?i)window\\.location\\.assign\\s*\\(\\s*[\"']([^\"']+)[\"']\\s*\\)";
        if (ReUtil.contains(locationAssignPattern, content)) {
            String redirectUrl = ReUtil.get(locationAssignPattern, content, 1);
            if (StrUtil.isNotBlank(redirectUrl)) {
                result.setRedirectType("js_window_location_assign");
                result.setRedirectUrl(resolveUrl(originalUrl, redirectUrl));
            }
        }

        // 4. meta refresh 重定向
        String metaRefreshPattern = "(?i)<meta[^>]+http-equiv\\s*=\\s*[\"']refresh[\"'][^>]+content\\s*=\\s*[\"'][^\"']*url\\s*=\\s*([^\"'>\\s]+)";
        if (ReUtil.contains(metaRefreshPattern, content)) {
            String redirectUrl = ReUtil.get(metaRefreshPattern, content, 1);
            if (StrUtil.isNotBlank(redirectUrl)) {
                result.setRedirectType("meta_refresh");
                result.setRedirectUrl(resolveUrl(originalUrl, redirectUrl));
            }
        }
    }

    /**
     * 解析相对URL为绝对URL
     */
    private String resolveUrl(String baseUrl, String relativeUrl) {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }

        if (relativeUrl.startsWith("//")) {
            // 协议相对URL
            if (baseUrl.startsWith("https://")) {
                return "https:" + relativeUrl;
            } else {
                return "http:" + relativeUrl;
            }
        }

        if (relativeUrl.startsWith("/")) {
            // 绝对路径
            try {
                java.net.URL base = new java.net.URL(baseUrl);
                return base.getProtocol() + "://" + base.getHost() + relativeUrl;
            } catch (Exception e) {
                return baseUrl + relativeUrl;
            }
        }

        // 相对路径
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            java.net.URL resolved = new java.net.URL(base, relativeUrl);
            return resolved.toString();
        } catch (Exception e) {
            return baseUrl + "/" + relativeUrl;
        }
    }

    /**
     * 获取结果类
     */
    public static class FetchResult {
        private String originalUrl;
        private boolean success;
        private String errorMessage;
        private String rawContent;
        private String redirectType;  // 重定向类型
        private String redirectUrl;   // 重定向目标URL
        private Document document;    // 解析后的文档

        // Getters and Setters
        public String getOriginalUrl() { return originalUrl; }
        public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public String getRawContent() { return rawContent; }
        public void setRawContent(String rawContent) { this.rawContent = rawContent; }

        public String getRedirectType() { return redirectType; }
        public void setRedirectType(String redirectType) { this.redirectType = redirectType; }

        public String getRedirectUrl() { return redirectUrl; }
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

        public Document getDocument() { return document; }
        public void setDocument(Document document) { this.document = document; }

        /**
         * 是否发生了重定向
         */
        public boolean isRedirected() {
            return StrUtil.isNotBlank(redirectUrl);
        }

        @Override
        public String toString() {
            return "FetchResult{" +
                    "originalUrl='" + originalUrl + '\'' +
                    ", success=" + success +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", redirectType='" + redirectType + '\'' +
                    ", redirectUrl='" + redirectUrl + '\'' +
                    ", isRedirected=" + isRedirected() +
                    '}';
        }
    }
}