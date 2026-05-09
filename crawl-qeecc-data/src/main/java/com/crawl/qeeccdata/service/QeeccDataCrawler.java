package com.crawl.qeeccdata.service;

import com.crawl.qeeccdata.model.CrawlProgressManager;
import com.crawl.qeeccdata.model.CrawlSong;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 核心爬取引擎 - 三层分离编排
 * Layer1(索引发现) → Layer2+3(元数据采集+音频下载) 交替执行
 *
 * 支持按版块触发、全部触发、断点续传
 */
@Service
public class QeeccDataCrawler {

    @Value("${qeecc.local.store-path:/Volumes/files/my/qeecc}")
    private String storePath;

    private final IndexDiscovery indexDiscovery;
    private final SongDownloader songDownloader;
    private final QeeccHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private CrawlProgressManager progressManager;
    private final AtomicBoolean crawling = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();

    /** 最多爬取的歌手页数(防止全量24582歌手耗时过长) */
    private static final int MAX_SINGER_PAGES = 5;
    /** 最多爬取的歌单页数 */
    private static final int MAX_PLAYLIST_PAGES = 5;

    public QeeccDataCrawler(IndexDiscovery indexDiscovery, SongDownloader songDownloader,
                            QeeccHttpClient httpClient) {
        this.indexDiscovery = indexDiscovery;
        this.songDownloader = songDownloader;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        Path storeDir = Paths.get(storePath);
        if (!Files.exists(storeDir)) {
            try {
                Files.createDirectories(storeDir);
            } catch (Exception e) {
                System.out.println("[Crawler] 创建存储目录失败: " + e.getMessage());
            }
        }
        this.progressManager = new CrawlProgressManager(storeDir);
        System.out.println("[Crawler] 初始化完成, 存储路径: " + storePath);
    }

    // ==================== 爬取触发 ====================

    /**
     * 爬取指定版块(直接列表页模式)
     */
    public Map<String, Object> crawlSection(String sectionName) {
        if (!crawling.compareAndSet(false, true)) {
            return Map.of("error", "爬取正在进行中, 请稍后再试");
        }

        try {
            log("开始爬取版块: " + sectionName);
            long startTime = System.currentTimeMillis();

            Map<String, String> directSections = indexDiscovery.getDirectSections();
            String entryPath = directSections.get(sectionName);

            if (entryPath != null) {
                // 模式A: 直接列表页
                crawlDirectSection(sectionName, entryPath);
            } else if ("歌手".equals(sectionName)) {
                // 模式B: 歌手
                crawlSingerSection();
            } else if ("歌单".equals(sectionName)) {
                // 模式B: 歌单
                crawlPlaylistSection();
            } else {
                log("未知版块: " + sectionName);
                return Map.of("error", "未知版块: " + sectionName);
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log("版块 [" + sectionName + "] 爬取完成, 耗时 " + (elapsed / 1000) + " 秒");

            return Map.of(
                    "section", sectionName,
                    "elapsed", elapsed / 1000 + "s",
                    "downloaded", progressManager.getDownloadedCount(),
                    "stats", progressManager.getSummary()
            );
        } finally {
            crawling.set(false);
        }
    }

    /**
     * 爬取所有版块
     */
    public Map<String, Object> crawlAll() {
        if (!crawling.compareAndSet(false, true)) {
            return Map.of("error", "爬取正在进行中, 请稍后再试");
        }

        try {
            log("========== 开始全量爬取 ==========");
            long startTime = System.currentTimeMillis();

            // Phase 1: 直接列表页版块
            for (Map.Entry<String, String> entry : indexDiscovery.getDirectSections().entrySet()) {
                crawlDirectSection(entry.getKey(), entry.getValue());
            }

            // Phase 2: 歌手
            crawlSingerSection();

            // Phase 3: 歌单
            crawlPlaylistSection();

            long elapsed = System.currentTimeMillis() - startTime;
            log("========== 全量爬取完成, 耗时 " + (elapsed / 1000) + " 秒 ==========");

            return Map.of(
                    "elapsed", elapsed / 1000 + "s",
                    "downloaded", progressManager.getDownloadedCount(),
                    "stats", progressManager.getSummary()
            );
        } finally {
            crawling.set(false);
        }
    }

    // ==================== 内部实现 ====================

    /**
     * 模式A: 爬取直接列表页版块
     */
    private void crawlDirectSection(String sectionName, String entryPath) {
        log("[" + sectionName + "] 开始索引发现...");
        List<CrawlSong> songs = indexDiscovery.discoverDirectSection(sectionName, entryPath, this::log);
        log("[" + sectionName + "] 索引发现完成, 共 " + songs.size() + " 首");

        // 去重 + 下载
        downloadSongs(songs, sectionName);

        progressManager.markSectionCompleted(sectionName);
    }

    /**
     * 模式B: 爬取歌手版块
     */
    private void crawlSingerSection() {
        log("[歌手] 开始索引发现...");
        String entryPath = indexDiscovery.getTwoLevelSections().get("歌手");

        // 第一层: 获取歌手列表
        List<IndexDiscovery.SingerInfo> singers = indexDiscovery.discoverSingerList(
                entryPath, MAX_SINGER_PAGES, this::log);
        log("[歌手] 发现 " + singers.size() + " 个歌手");

        // 第二层: 逐个歌手爬取歌曲
        for (IndexDiscovery.SingerInfo singer : singers) {
            List<CrawlSong> songs = indexDiscovery.discoverSingerSongs(singer, this::log);
            downloadSongs(songs, "歌手");
            httpClient.rateLimit();
        }

        progressManager.markSectionCompleted("歌手");
    }

    /**
     * 模式B: 爬取歌单版块
     */
    private void crawlPlaylistSection() {
        log("[歌单] 开始索引发现...");
        String entryPath = indexDiscovery.getTwoLevelSections().get("歌单");

        // 第一层: 获取歌单列表
        List<IndexDiscovery.PlaylistInfo> playlists = indexDiscovery.discoverPlaylistList(
                entryPath, MAX_PLAYLIST_PAGES, this::log);
        log("[歌单] 发现 " + playlists.size() + " 个歌单");

        // 第二层: 逐个歌单爬取歌曲
        for (IndexDiscovery.PlaylistInfo playlist : playlists) {
            List<CrawlSong> songs = indexDiscovery.discoverPlaylistSongs(playlist, this::log);
            downloadSongs(songs, "歌单");
            httpClient.rateLimit();
        }

        progressManager.markSectionCompleted("歌单");
    }

    /**
     * 批量下载歌曲(去重 + 逐首下载)
     */
    private void downloadSongs(List<CrawlSong> songs, String sectionName) {
        Set<String> seen = new HashSet<>();
        int downloaded = 0;
        int skipped = 0;
        int failed = 0;

        for (CrawlSong song : songs) {
            // 全局去重
            if (seen.contains(song.songId())) continue;
            seen.add(song.songId());

            // 跳过已下载
            if (progressManager.isDownloaded(song.songId())) {
                skipped++;
                continue;
            }

            // 下载
            CrawlSong result = songDownloader.downloadSong(song, progressManager, this::log);
            if (result.isDownloaded()) {
                downloaded++;
            } else {
                failed++;
            }

            // 速率控制
            httpClient.rateLimit();
        }

        log("[" + sectionName + "] 下载完成: 成功=" + downloaded + " 跳过=" + skipped + " 失败=" + failed);
    }

    // ==================== 状态查询 ====================

    public boolean isCrawling() {
        return crawling.get();
    }

    public Map<String, Object> getProgress() {
        Map<String, Object> result = new LinkedHashMap<>(progressManager.getSummary());
        result.put("crawling", crawling.get());
        result.put("storePath", storePath);

        // 最近日志
        List<String> recentLogs = new ArrayList<>();
        Iterator<String> it = logQueue.iterator();
        while (it.hasNext() && recentLogs.size() < 20) {
            recentLogs.add(it.next());
        }
        result.put("recentLogs", recentLogs);

        return result;
    }

    /**
     * 获取可用的版块列表
     */
    public List<Map<String, String>> getAvailableSections() {
        List<Map<String, String>> sections = new ArrayList<>();
        for (Map.Entry<String, String> entry : indexDiscovery.getDirectSections().entrySet()) {
            sections.add(Map.of("name", entry.getKey(), "type", "direct", "entry", entry.getValue()));
        }
        for (Map.Entry<String, String> entry : indexDiscovery.getTwoLevelSections().entrySet()) {
            sections.add(Map.of("name", entry.getKey(), "type", "two-level", "entry", entry.getValue()));
        }
        return sections;
    }

    // ==================== 日志 ====================

    private void log(String message) {
        System.out.println("[Crawler] " + message);
        logQueue.add(message);
        // 保留最近100条日志
        while (logQueue.size() > 100) {
            logQueue.poll();
        }
    }
}
