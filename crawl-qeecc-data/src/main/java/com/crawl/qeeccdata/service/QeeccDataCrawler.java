package com.crawl.qeeccdata.service;

import com.crawl.qeeccdata.model.CrawlProgressManager;
import com.crawl.qeeccdata.model.CrawlSong;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Value("${qeecc.crawl.max-threads:4}")
    private int maxThreads;

    private final IndexDiscovery indexDiscovery;
    private final SongDownloader songDownloader;
    private final QeeccHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private CrawlProgressManager progressManager;
    private ExecutorService crawlExecutor;
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
        this.crawlExecutor = Executors.newFixedThreadPool(maxThreads,
                r -> {
                    Thread t = new Thread(r, "crawl-worker-" + System.currentTimeMillis() % 10000);
                    t.setDaemon(true);
                    return t;
                });
        System.out.println("[Crawler] 初始化完成, 存储路径: " + storePath + ", 爬取线程数: " + maxThreads);
    }

    @PreDestroy
    public void shutdown() {
        if (crawlExecutor != null) {
            crawlExecutor.shutdown();
            try {
                if (!crawlExecutor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    crawlExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                crawlExecutor.shutdownNow();
            }
        }
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
     * 批量下载歌曲(去重 + 多线程并发下载, 最多4线程)
     */
    private void downloadSongs(List<CrawlSong> songs, String sectionName) {
        Set<String> seen = new HashSet<>();
        AtomicInteger downloaded = new AtomicInteger(0);
        AtomicInteger skipped = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (CrawlSong song : songs) {
            // 全局去重 (单线程迭代, 无需并发集合)
            if (seen.contains(song.songId())) continue;
            seen.add(song.songId());

            // 跳过已下载
            if (progressManager.isDownloaded(song.songId())) {
                skipped.incrementAndGet();
                continue;
            }

            // 多线程并发下载
            futures.add(CompletableFuture.runAsync(() -> {
                CrawlSong result = songDownloader.downloadSong(song, progressManager, this::log);
                if (result.isDownloaded()) {
                    downloaded.incrementAndGet();
                } else {
                    failed.incrementAndGet();
                }
            }, crawlExecutor));
        }

        // 等待所有下载任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log("[" + sectionName + "] 下载完成: 成功=" + downloaded.get() + " 跳过=" + skipped.get() + " 失败=" + failed.get());
    }

    // ==================== 歌词修复 ====================

    /**
     * 批量修复已下载歌曲的歌词
     * 扫描所有 metadata.json, 对缺少 localLrcFile 的歌曲下载歌词 + 更新 metadata
     * 仅下载歌词, 不重复下载音频/封面
     */
    public Map<String, Object> repairLyrics() {
        if (!crawling.compareAndSet(false, true)) {
            log("[歌词修复] 爬取正在进行中, 无法启动歌词修复");
            return Map.of("error", "爬取正在进行中, 请稍后再试");
        }

        try {
            log("========== 开始批量修复歌词 ==========");
            long startTime = System.currentTimeMillis();

            AtomicInteger repaired = new AtomicInteger(0);
            AtomicInteger skipped = new AtomicInteger(0);
            AtomicInteger failed = new AtomicInteger(0);
            AtomicInteger noAudio = new AtomicInteger(0);

            Path storeDir = Paths.get(storePath);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // 扫描所有版块目录
            List<Path> sectionDirs;
            try (java.util.stream.Stream<Path> dirStream = Files.list(storeDir)) {
                sectionDirs = dirStream.filter(Files::isDirectory).toList();
            } catch (IOException e) {
                log("[歌词修复] 扫描存储目录失败: " + e.getMessage());
                throw new RuntimeException("扫描存储目录失败: " + e.getMessage(), e);
            }
            log("[歌词修复] 发现 " + sectionDirs.size() + " 个版块目录");

            for (Path sectionDir : sectionDirs) {
                String sectionName = sectionDir.getFileName().toString();
                Path metaFile = sectionDir.resolve("metadata.json");
                if (!Files.exists(metaFile)) {
                    log("[歌词修复] 跳过 " + sectionName + " (无 metadata.json)");
                    continue;
                }

                try {
                    String json = Files.readString(metaFile);
                    List<Map<String, Object>> metas = objectMapper.readValue(json, listOfMapsType());
                    log("[歌词修复] 扫描版块 [" + sectionName + "]: 共 " + metas.size() + " 首歌曲");

                    int needRepair = 0;
                    for (Map<String, Object> meta : metas) {
                        // 跳过已有歌词的
                        if (meta.get("localLrcFile") != null) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        // 跳过未下载音频的
                        if (meta.get("localAudioFile") == null) {
                            noAudio.incrementAndGet();
                            continue;
                        }

                        String songId = (String) meta.get("songId");
                        String songName = (String) meta.get("songName");
                        if (songId == null) continue;

                        needRepair++;
                        // 多线程下载歌词
                        futures.add(CompletableFuture.runAsync(() -> {
                            try {
                                log("[歌词修复] 开始下载歌词: " + songName + " (" + songId + ")");
                                // 全局速率控制
                                httpClient.globalRateLimit();
                                String lrcContent = httpClient.getLyrics(songId);
                                if (lrcContent != null) {
                                    Path lrcPath = sectionDir.resolve(songId + ".lrc");
                                    Files.writeString(lrcPath, lrcContent);
                                    meta.put("localLrcFile", songId + ".lrc");
                                    repaired.incrementAndGet();
                                    log("[歌词修复] ✓ 歌词下载成功: " + songName + " (" + songId + ") → " + lrcPath.getFileName());
                                } else {
                                    failed.incrementAndGet();
                                    log("[歌词修复] ✗ 歌词不可用: " + songName + " (" + songId + ") - 接口返回空");
                                }
                            } catch (Exception e) {
                                failed.incrementAndGet();
                                log("[歌词修复] ✗ 歌词下载异常: " + songId + " → " + e.getMessage());
                            }
                        }, crawlExecutor));
                    }
                    log("[歌词修复] 版块 [" + sectionName + "]: 需修复 " + needRepair + " 首, 已有歌词 " + (metas.size() - needRepair - noAudio.get()) + " 首");
                } catch (Exception e) {
                    log("[歌词修复] 读取 metadata 失败: " + sectionName + " → " + e.getMessage());
                }
            }

            log("[歌词修复] 等待 " + futures.size() + " 个歌词下载任务完成...");
            // 等待所有歌词下载完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log("[歌词修复] 所有歌词下载任务已完成");

            // 统一写回所有 metadata.json (因为歌词已下载完毕, 现在更新文件)
            int metadataUpdated = 0;
            for (Path sectionDir : sectionDirs) {
                Path metaFile = sectionDir.resolve("metadata.json");
                if (!Files.exists(metaFile)) continue;
                try {
                    String json = Files.readString(metaFile);
                    List<Map<String, Object>> metas = objectMapper.readValue(json, listOfMapsType());
                    boolean changed = false;
                    for (Map<String, Object> meta : metas) {
                        String songId = (String) meta.get("songId");
                        if (songId == null) continue;
                        // 如果 .lrc 文件已存在但 metadata 未记录, 则补上
                        if (meta.get("localLrcFile") == null && Files.exists(sectionDir.resolve(songId + ".lrc"))) {
                            meta.put("localLrcFile", songId + ".lrc");
                            changed = true;
                        }
                    }
                    if (changed) {
                        objectMapper.writeValue(metaFile.toFile(), metas);
                        metadataUpdated++;
                        log("[歌词修复] 更新 metadata: " + sectionDir.getFileName());
                    }
                } catch (Exception e) {
                    log("[歌词修复] 更新 metadata 失败: " + sectionDir.getFileName() + " → " + e.getMessage());
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log("========== 歌词修复完成: 修复=" + repaired.get() + " 已有歌词=" + skipped.get()
                    + " 无音频=" + noAudio.get() + " 失败=" + failed.get()
                    + ", 更新metadata=" + metadataUpdated
                    + ", 耗时 " + (elapsed / 1000) + " 秒 ==========");

            return Map.of(
                    "repaired", repaired.get(),
                    "skipped", skipped.get(),
                    "noAudio", noAudio.get(),
                    "failed", failed.get(),
                    "metadataUpdated", metadataUpdated,
                    "elapsed", elapsed / 1000 + "s"
            );
        } finally {
            crawling.set(false);
        }
    }

    /**
     * 单曲补歌词
     * 根据歌曲详情页路径下载歌词, 保存到对应版块目录
     */
    public Map<String, Object> fetchLyrics(String songId) {
        try {
            log("[单曲补歌词] 开始查找歌曲: " + songId);
            Path storeDir = Paths.get(storePath);

            // 搜索所有 metadata.json 找到该歌曲
            try (java.util.stream.Stream<Path> dirs = Files.list(storeDir)) {
                List<Path> sectionDirs = dirs.filter(Files::isDirectory).toList();
                log("[单曲补歌词] 扫描 " + sectionDirs.size() + " 个版块目录");

                for (Path sectionDir : sectionDirs) {
                    String sectionName = sectionDir.getFileName().toString();
                    Path metaFile = sectionDir.resolve("metadata.json");
                    if (!Files.exists(metaFile)) continue;

                    String json = Files.readString(metaFile);
                    List<Map<String, Object>> metas = objectMapper.readValue(json, listOfMapsType());

                    for (Map<String, Object> meta : metas) {
                        if (songId.equals(meta.get("songId"))) {
                            String songName = (String) meta.get("songName");
                            log("[单曲补歌词] 找到歌曲: " + songName + " (" + songId + ") 在版块 [" + sectionName + "]");

                            // 已有歌词
                            if (meta.get("localLrcFile") != null && Files.exists(sectionDir.resolve((String) meta.get("localLrcFile")))) {
                                log("[单曲补歌词] 歌词已存在: " + meta.get("localLrcFile"));
                                return Map.of("status", "already_exists", "songId", songId,
                                        "songName", songName != null ? songName : "",
                                        "localLrcFile", meta.get("localLrcFile"));
                            }

                            // 下载歌词
                            log("[单曲补歌词] 开始下载歌词: " + songName + " (" + songId + ")");
                            httpClient.globalRateLimit();
                            String lrcContent = httpClient.getLyrics(songId);
                            if (lrcContent != null) {
                                String lrcFileName = songId + ".lrc";
                                Path lrcPath = sectionDir.resolve(lrcFileName);
                                Files.writeString(lrcPath, lrcContent);
                                meta.put("localLrcFile", lrcFileName);
                                objectMapper.writeValue(metaFile.toFile(), metas);
                                log("[单曲补歌词] ✓ 歌词下载成功: " + songName + " → " + lrcFileName + " (" + lrcContent.length() + " 字符)");
                                return Map.of("status", "downloaded", "songId", songId,
                                        "songName", songName != null ? songName : "",
                                        "localLrcFile", lrcFileName);
                            } else {
                                log("[单曲补歌词] ✗ 歌词不可用: " + songName + " (" + songId + ") - 接口返回空");
                                return Map.of("status", "not_available", "songId", songId,
                                        "songName", songName != null ? songName : "");
                            }
                        }
                    }
                }
            }
            log("[单曲补歌词] 歌曲未找到: " + songId);
            return Map.of("error", "歌曲未找到: " + songId);
        } catch (Exception e) {
            log("[单曲补歌词] ✗ 歌词下载异常: " + songId + " → " + e.getMessage());
            return Map.of("error", "歌词下载失败: " + e.getMessage());
        }
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

    private com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>> listOfMapsType() {
        return new com.fasterxml.jackson.core.type.TypeReference<>() {};
    }
}
