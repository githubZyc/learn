package com.crawl.qeeccdata.service;

import com.crawl.qeeccdata.model.CrawlProgressManager;
import com.crawl.qeeccdata.model.CrawlSong;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Layer2 + Layer3 - 元数据采集 + 音频下载
 * 对每个 songId: 调 Play API 获取 mp3Url → 立即下载音频 + 封面 → 保存 metadata
 */
@Service
public class SongDownloader {

    @Value("${qeecc.local.store-path:/Volumes/files/my/qeecc}")
    private String storePath;

    @Value("${qeecc.crawl.max-concurrent-downloads:3}")
    private int maxConcurrentDownloads;

    private final QeeccHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Semaphore downloadSemaphore;

    public SongDownloader(QeeccHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.downloadSemaphore = new Semaphore(3); // 默认3, @Value注入后不生效, 在init里重设
    }

    /**
     * 下载一首歌曲: Play API + 音频下载 + 封面下载 + metadata 保存
     * 返回下载后的 CrawlSong (填充了 Layer2+Layer3 数据)
     */
    public CrawlSong downloadSong(CrawlSong song, CrawlProgressManager progress,
                                  java.util.function.Consumer<String> onProgress) {
        // 跳过已下载
        if (progress.isDownloaded(song.songId())) {
            return song;
        }

        try {
            downloadSemaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return song;
        }

        try {
            // Layer2: 获取播放链接
            if (!song.hasPlayResult()) {
                if (onProgress != null) onProgress.accept("获取播放链接: " + song.songName() + " - " + song.singer());
                QeeccHttpClient.PlayResult playResult = httpClient.getPlayUrl(song.songId());
                if (playResult == null) {
                    System.out.println("[Downloader] 获取播放链接失败: " + song.songId());
                    progress.incrementStat("playApiFail");
                    return song;
                }
                song = song.withPlayResult(playResult.mp3Url(), playResult.pic(), playResult.lkid());
                progress.markPlayUrlFetched(song.songId());
                httpClient.rateLimit();
            }

            // 确保版块目录存在
            String sectionDir = song.source();
            Path sectionPath = Paths.get(storePath, sectionDir);
            if (!Files.exists(sectionPath)) {
                Files.createDirectories(sectionPath);
            }

            // Layer3: 下载音频文件
            String mp3Url = song.mp3Url();
            String ext = determineExtension(mp3Url);
            String audioFileName = song.songId() + ext;
            Path audioPath = sectionPath.resolve(audioFileName);

            if (!Files.exists(audioPath)) {
                if (onProgress != null) onProgress.accept("下载: " + song.songName() + " (" + ext + ")");
                boolean success = httpClient.downloadFile(mp3Url, audioPath,
                        httpClient.getBaseUrl() + "/song/" + song.songId() + ".html");
                if (!success) {
                    System.out.println("[Downloader] 音频下载失败: " + song.songId());
                    progress.incrementStat("downloadFail");
                    return song;
                }
            }

            long fileSize = Files.size(audioPath);
            String downloadTime = LocalDateTime.now().toString();

            // 下载封面图(如果有)
            String picFileName = null;
            if (song.pic() != null && !song.pic().isEmpty()) {
                picFileName = song.songId() + ".jpg";
                Path picPath = sectionPath.resolve(picFileName);
                if (!Files.exists(picPath)) {
                    httpClient.downloadFile(song.pic(), picPath, httpClient.getBaseUrl() + "/");
                }
            }

            song = song.withDownloadResult(audioFileName, picFileName, fileSize, downloadTime);
            progress.markDownloaded(song.songId());
            progress.incrementStat("downloaded");

            // 更新该版块的 metadata.json
            appendMetadata(sectionPath, song);

            System.out.println("[Downloader] 下载完成: " + song.songName() + " - " + song.singer()
                    + " (" + formatSize(fileSize) + ")");

        } catch (Exception e) {
            System.out.println("[Downloader] 下载异常: " + song.songId() + " → " + e.getMessage());
            progress.incrementStat("downloadError");
        } finally {
            downloadSemaphore.release();
        }

        return song;
    }

    /**
     * 判断音频文件扩展名
     */
    private String determineExtension(String url) {
        if (url.contains(".mp3")) return ".mp3";
        return ".m4a"; // 默认
    }

    /**
     * 追加歌曲元数据到版块的 metadata.json
     */
    private synchronized void appendMetadata(Path sectionPath, CrawlSong song) {
        try {
            Path metaFile = sectionPath.resolve("metadata.json");
            List<Map<String, Object>> metas;

            if (Files.exists(metaFile)) {
                String json = Files.readString(metaFile);
                metas = new ArrayList<>(objectMapper.readValue(json, listOfMapsType()));
            } else {
                metas = new ArrayList<>();
            }

            // 去重: 如果 songId 已存在则更新
            metas.removeIf(m -> song.songId().equals(m.get("songId")));

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("songId", song.songId());
            meta.put("songName", song.songName());
            meta.put("singer", song.singer());
            meta.put("source", song.source());
            meta.put("sourceDetail", song.sourceDetail());
            meta.put("localAudioFile", song.localAudioFile());
            meta.put("localPicFile", song.localPicFile());
            meta.put("fileSize", song.fileSize());
            meta.put("downloadTime", song.downloadTime());
            metas.add(meta);

            // 按下载时间倒序
            metas.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    String.valueOf(b.getOrDefault("downloadTime", "")),
                    String.valueOf(a.getOrDefault("downloadTime", ""))));

            objectMapper.writeValue(metaFile.toFile(), metas);
        } catch (Exception e) {
            System.out.println("[Downloader] 保存 metadata 失败: " + e.getMessage());
        }
    }

    private com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>> listOfMapsType() {
        return new com.fasterxml.jackson.core.type.TypeReference<>() {};
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
