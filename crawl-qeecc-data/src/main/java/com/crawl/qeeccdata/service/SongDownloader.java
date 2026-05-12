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

/**
 * Layer2 + Layer3 - 元数据采集 + 音频下载
 * 对每个 songId: 调 Play API 获取 mp3Url → 立即下载音频 + 封面 → 保存 metadata
 */
@Service
public class SongDownloader {

    @Value("${qeecc.local.store-path:/Volumes/files/my/qeecc}")
    private String storePath;

    private final QeeccHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SongDownloader(QeeccHttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /** 下载 403 时的最大重试次数 (每次重新获取播放链接) */
    private static final int MAX_DOWNLOAD_RETRIES = 2;

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
            // Layer2: 获取播放链接
            if (!song.hasPlayResult()) {
                song = fetchPlayUrl(song, progress, onProgress);
                if (!song.hasPlayResult()) return song;
            }

            // 确保版块目录存在
            String sectionDir = song.source();
            Path sectionPath = Paths.get(storePath, sectionDir);
            if (!Files.exists(sectionPath)) {
                Files.createDirectories(sectionPath);
            }

            // Layer3: 下载音频文件 (含 403 重试: 重新获取播放链接)
            String mp3Url = song.mp3Url();
            String ext = determineExtension(mp3Url);
            String audioFileName = song.songId() + ext;
            Path audioPath = sectionPath.resolve(audioFileName);

            if (!Files.exists(audioPath)) {
                if (onProgress != null) onProgress.accept("下载: " + song.songName() + " (" + ext + ")");
                boolean success = httpClient.downloadFile(mp3Url, audioPath,
                        httpClient.getBaseUrl() + "/song/" + song.songId() + ".html");

                // 403 重试: 可能是 CDN token 过期, 重新获取播放链接后重试
                int retryCount = 0;
                while (!success && retryCount < MAX_DOWNLOAD_RETRIES) {
                    retryCount++;
                    System.out.println("[Downloader] 下载 403 重试 (" + retryCount + "/" + MAX_DOWNLOAD_RETRIES + "): 重新获取播放链接 " + song.songId());
                    song = song.withoutPlayResult(); // 清除旧播放结果
                    song = fetchPlayUrl(song, progress, onProgress);
                    if (!song.hasPlayResult()) break;

                    mp3Url = song.mp3Url();
                    ext = determineExtension(mp3Url);
                    audioFileName = song.songId() + ext;
                    audioPath = sectionPath.resolve(audioFileName);
                    if (Files.exists(audioPath)) {
                        success = true;
                        break;
                    }
                    success = httpClient.downloadFile(mp3Url, audioPath,
                            httpClient.getBaseUrl() + "/song/" + song.songId() + ".html");
                }

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

            // 下载歌词(自动同步, 与音频同名 .lrc)
            String lrcFileName = null;
            Path lrcPath = sectionPath.resolve(song.songId() + ".lrc");
            if (!Files.exists(lrcPath)) {
                String lrcContent = httpClient.getLyrics(song.songId());
                if (lrcContent != null) {
                    Files.writeString(lrcPath, lrcContent);
                    lrcFileName = song.songId() + ".lrc";
                    if (onProgress != null) onProgress.accept("歌词下载: " + song.songName());
                } else {
                    System.out.println("[Downloader] 歌词不可用: " + song.songId());
                }
            } else {
                lrcFileName = song.songId() + ".lrc";
            }

            song = song.withDownloadResult(audioFileName, picFileName, lrcFileName, fileSize, downloadTime);
            progress.markDownloaded(song.songId());
            progress.incrementStat("downloaded");

            // 更新该版块的 metadata.json
            appendMetadata(sectionPath, song);

            System.out.println("[Downloader] 下载完成: " + song.songName() + " - " + song.singer()
                    + " (" + formatSize(fileSize) + ")");

        } catch (Exception e) {
            System.out.println("[Downloader] 下载异常: " + song.songId() + " → " + e.getMessage());
            progress.incrementStat("downloadError");
        }

        return song;
    }

    /**
     * 获取播放链接 (Layer2)
     * 使用 globalRateLimit() 在请求前等待, 确保多线程下 Play API 请求间隔
     */
    private CrawlSong fetchPlayUrl(CrawlSong song, CrawlProgressManager progress,
                                   java.util.function.Consumer<String> onProgress) {
        // 全局速率控制: 请求前等待, 防止多线程并发导致高频请求触发反爬
        httpClient.globalRateLimit();

        if (onProgress != null) onProgress.accept("获取播放链接: " + song.songName() + " - " + song.singer());
        QeeccHttpClient.PlayResult playResult = httpClient.getPlayUrl(song.songId());
        if (playResult == null) {
            System.out.println("[Downloader] 获取播放链接失败: " + song.songId());
            progress.incrementStat("playApiFail");
            return song;
        }
        song = song.withPlayResult(playResult.mp3Url(), playResult.pic(), playResult.lkid());
        progress.markPlayUrlFetched(song.songId());
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
            meta.put("localLrcFile", song.localLrcFile());
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
