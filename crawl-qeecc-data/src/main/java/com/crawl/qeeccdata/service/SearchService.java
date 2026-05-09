package com.crawl.qeeccdata.service;

import com.crawl.qeeccdata.model.CrawlProgressManager;
import com.crawl.qeeccdata.model.CrawlSong;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 站内搜索服务
 * 利用 qeecc.com 搜索接口 /so/{keyword}.html 搜索歌曲
 * 支持搜索结果列表展示 + 选中歌曲下载
 */
@Service
public class SearchService {

    @Value("${qeecc.local.store-path:/Volumes/files/my/qeecc}")
    private String storePath;

    private final QeeccHttpClient httpClient;
    private final SongDownloader songDownloader;
    private CrawlProgressManager progressManager;

    public SearchService(QeeccHttpClient httpClient, SongDownloader songDownloader) {
        this.httpClient = httpClient;
        this.songDownloader = songDownloader;
    }

    /**
     * 延迟初始化进度管理器 (与 Crawler 共享存储目录)
     */
    private synchronized CrawlProgressManager getProgressManager() {
        if (progressManager == null) {
            Path storeDir = Paths.get(storePath);
            if (!Files.exists(storeDir)) {
                try { Files.createDirectories(storeDir); } catch (Exception ignored) {}
            }
            progressManager = new CrawlProgressManager(storeDir);
        }
        return progressManager;
    }

    // ==================== 搜索歌曲 ====================

    /**
     * 搜索歌曲
     * 访问 /so/{keyword}.html, 解析搜索结果列表
     */
    public List<SearchSongResult> search(String keyword) {
        List<SearchSongResult> results = new ArrayList<>();
        try {
            String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String searchUrl = httpClient.getBaseUrl() + "/so/" + encoded + ".html";

            // 使用两步访问获取搜索页 (JS 重定向反爬)
            Document doc = httpClient.fetchSearchPage(searchUrl);
            if (doc == null) {
                System.out.println("[Search] 搜索页获取失败: " + searchUrl);
                return results;
            }

            // 解析搜索结果: .play_list ul li .name a (与列表页一致)
            Elements items = doc.select(".play_list ul li .name a");
            for (Element item : items) {
                String href = item.attr("href");
                String text = item.text().trim();
                if (href != null && href.contains("/song/") && !text.isEmpty()) {
                    String songId = httpClient.extractSongId(href);
                    String[] parsed = httpClient.parseSongText(text);
                    results.add(new SearchSongResult(songId, parsed[1], parsed[0], href, text));
                }
            }

            System.out.println("[Search] 搜索 '" + keyword + "' 找到 " + results.size() + " 首歌曲");
        } catch (Exception e) {
            System.out.println("[Search] 搜索异常: " + e.getMessage());
        }
        return results;
    }

    // ==================== 下载歌曲 ====================

    /**
     * 下载指定歌曲 (根据 songId)
     * 复用现有 SongDownloader 的完整下载流程: 获取播放链接 → 下载音频 → 保存元数据
     */
    public DownloadResult download(String songId) {
        try {
            // 构建 CrawlSong (搜索下载归入 "搜索下载" 版块)
            CrawlSong song = CrawlSong.forIndex(songId, "", "", "搜索下载", "搜索下载",
                    "/song/" + songId + ".html", "");

            // 调用 SongDownloader 完整流程 (含 Play API + 音频下载 + 封面 + metadata)
            CrawlSong result = songDownloader.downloadSong(song, getProgressManager(), msg -> {});

            if (result.isDownloaded()) {
                return new DownloadResult(true, "下载成功: " + result.songName() + " - " + result.singer(),
                        result.songId(), result.songName(), result.singer(), result.localAudioFile(), result.fileSize());
            } else {
                return new DownloadResult(false, "下载失败: " + songId, songId, "", "", null, 0);
            }
        } catch (Exception e) {
            return new DownloadResult(false, "下载异常: " + e.getMessage(), songId, "", "", null, 0);
        }
    }

    // ==================== 数据结构 ====================

    /**
     * 搜索结果
     */
    public record SearchSongResult(
            String songId,
            String songName,
            String singer,
            String href,
            String fullText
    ) {}

    /**
     * 下载结果
     */
    public record DownloadResult(
            boolean success,
            String message,
            String songId,
            String songName,
            String singer,
            String localAudioFile,
            long fileSize
    ) {}
}
