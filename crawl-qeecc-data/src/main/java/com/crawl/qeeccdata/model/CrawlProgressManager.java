package com.crawl.qeeccdata.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 爬取进度管理器 - 支持断点续传
 * 持久化到 progress.json, 记录每个版块已爬到的页码和已下载的 songId
 */
public class CrawlProgressManager {

    private final ObjectMapper objectMapper;
    private final Path progressFile;

    /** 每个版块已爬到的页码 */
    private final ConcurrentHashMap<String, Integer> sectionPageProgress = new ConcurrentHashMap<>();

    /** 已完成全量索引的版块 */
    private final Set<String> completedSections = ConcurrentHashMap.newKeySet();

    /** 已下载的 songId 集合(全局去重) */
    private final Set<String> downloadedSongIds = ConcurrentHashMap.newKeySet();

    /** 已获取播放链接的 songId 集合 */
    private final Set<String> playUrlFetchedIds = ConcurrentHashMap.newKeySet();

    /** 统计 */
    private final ConcurrentHashMap<String, Long> stats = new ConcurrentHashMap<>();

    public CrawlProgressManager(Path storePath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.progressFile = storePath.resolve("progress.json");
        load();
    }

    // ==================== 页码进度 ====================

    public int getPageProgress(String section) {
        return sectionPageProgress.getOrDefault(section, 0);
    }

    public void setPageProgress(String section, int page) {
        sectionPageProgress.put(section, page);
        save();
    }

    // ==================== 版块完成标记 ====================

    public boolean isSectionCompleted(String section) {
        return completedSections.contains(section);
    }

    public void markSectionCompleted(String section) {
        completedSections.add(section);
        save();
    }

    // ==================== 歌曲去重 ====================

    public boolean isDownloaded(String songId) {
        return downloadedSongIds.contains(songId);
    }

    public void markDownloaded(String songId) {
        downloadedSongIds.add(songId);
        save();
    }

    public boolean isPlayUrlFetched(String songId) {
        return playUrlFetchedIds.contains(songId);
    }

    public void markPlayUrlFetched(String songId) {
        playUrlFetchedIds.add(songId);
    }

    public int getDownloadedCount() {
        return downloadedSongIds.size();
    }

    // ==================== 统计 ====================

    public void incrementStat(String key) {
        stats.merge(key, 1L, Long::sum);
    }

    public long getStat(String key) {
        return stats.getOrDefault(key, 0L);
    }

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("downloadedSongs", downloadedSongIds.size());
        summary.put("playUrlFetched", playUrlFetchedIds.size());
        summary.put("completedSections", completedSections);
        summary.put("stats", new LinkedHashMap<>(stats));
        summary.put("pageProgress", new LinkedHashMap<>(sectionPageProgress));
        return summary;
    }

    // ==================== 持久化 ====================

    @SuppressWarnings("unchecked")
    private void load() {
        if (!java.nio.file.Files.exists(progressFile)) return;
        try {
            String json = java.nio.file.Files.readString(progressFile);
            Map<String, Object> data = objectMapper.readValue(json, Map.class);

            List<String> completed = (List<String>) data.getOrDefault("completedSections", List.of());
            completedSections.addAll(completed);

            List<String> downloaded = (List<String>) data.getOrDefault("downloadedSongIds", List.of());
            downloadedSongIds.addAll(downloaded);

            List<String> fetched = (List<String>) data.getOrDefault("playUrlFetchedIds", List.of());
            playUrlFetchedIds.addAll(fetched);

            Map<String, Integer> pages = (Map<String, Integer>) data.getOrDefault("pageProgress", Map.of());
            sectionPageProgress.putAll(pages);

            Map<String, Number> statsMap = (Map<String, Number>) data.getOrDefault("stats", Map.of());
            statsMap.forEach((k, v) -> stats.put(k, v.longValue()));

            System.out.println("[Progress] 加载进度: 已下载 " + downloadedSongIds.size()
                    + " 首, 已完成版块 " + completedSections);
        } catch (Exception e) {
            System.out.println("[Progress] 加载进度失败: " + e.getMessage());
        }
    }

    private synchronized void save() {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("completedSections", new ArrayList<>(completedSections));
            data.put("downloadedSongIds", new ArrayList<>(downloadedSongIds));
            data.put("playUrlFetchedIds", new ArrayList<>(playUrlFetchedIds));
            data.put("pageProgress", new LinkedHashMap<>(sectionPageProgress));
            data.put("stats", new LinkedHashMap<>(stats));
            objectMapper.writeValue(progressFile.toFile(), data);
        } catch (Exception e) {
            System.out.println("[Progress] 保存进度失败: " + e.getMessage());
        }
    }
}
