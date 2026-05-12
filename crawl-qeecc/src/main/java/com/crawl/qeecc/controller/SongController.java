package com.crawl.qeecc.controller;

import com.crawl.qeecc.service.QeeccSearchService;
import com.crawl.qeecc.service.SongCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 歌曲数据 API
 * 提供热门歌曲列表、搜索、播放链接获取接口
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongCacheService songCacheService;

    @Value("${qeecc.local.store-path:/Volumes/files/my/qeecc}")
    private String storePath;

    public SongController(SongCacheService songCacheService) {
        this.songCacheService = songCacheService;
    }

    /**
     * 获取热门歌曲列表（预加载的缓存数据）
     * GET /api/songs/hot
     */
    @GetMapping("/hot")
    public List<SongCacheService.CachedSong> getHotSongs() {
        return songCacheService.getHotSongs();
    }

    /**
     * 搜索歌曲（实时搜索）
     * GET /api/songs/search?keyword=江南
     */
    @GetMapping("/search")
    public List<SongCacheService.CachedSong> search(@RequestParam String keyword) {
        return songCacheService.search(keyword);
    }

    /**
     * 获取播放链接（实时获取，链接有过期时间）
     * GET /api/songs/play/{songId}
     * 返回: { "mp3Url": "...", "title": "...", "pic": "..." }
     */
    @GetMapping("/play/{songId}")
    public Map<String, Object> getPlayUrl(@PathVariable String songId) {
        QeeccSearchService.PlayResult result = songCacheService.getPlayUrl(songId);
        if (result != null) {
            return Map.of(
                    "mp3Url", result.mp3Url(),
                    "title", result.title(),
                    "pic", result.pic(),
                    "lkid", result.lkid()
            );
        }
        return Map.of("error", "获取播放链接失败", "songId", songId);
    }

    // ==================== 本地音乐库 API ====================

    /**
     * 扫描本地所有子目录的 metadata.json，返回合并的歌曲列表
     * GET /api/songs/local
     */
    @GetMapping("/local")
    public List<Map<String, Object>> getLocalSongs() {
        List<Map<String, Object>> allSongs = new ArrayList<>();
        Path basePath = Paths.get(storePath);
        ObjectMapper mapper = new ObjectMapper();

        if (!Files.exists(basePath) || !Files.isDirectory(basePath)) {
            return allSongs;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(basePath)) {
            for (Path dir : stream) {
                if (Files.isDirectory(dir)) {
                    Path metaFile = dir.resolve("metadata.json");
                    if (Files.exists(metaFile)) {
                        String json = Files.readString(metaFile, StandardCharsets.UTF_8);
                        List<Map<String, Object>> songs = mapper.readValue(json, new TypeReference<>() {});
                        String category = dir.getFileName().toString();
                        for (Map<String, Object> song : songs) {
                            song.put("category", category);
                            allSongs.add(song);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[Local] 扫描本地目录失败: " + e.getMessage());
        }
        return allSongs;
    }

    /**
     * 获取本地音频文件（支持子目录查找）
     * GET /api/songs/file/{songId}
     */
    @GetMapping("/file/{songId}")
    public ResponseEntity<Resource> getAudioFile(@PathVariable String songId) {
        Path basePath = Paths.get(storePath);
        Path audioPath = null;

        try {
            List<Path> subDirs = Files.list(basePath)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            for (Path dir : subDirs) {
                Path m4a = dir.resolve(songId + ".m4a");
                Path mp3 = dir.resolve(songId + ".mp3");
                if (Files.exists(m4a)) { audioPath = m4a; break; }
                if (Files.exists(mp3)) { audioPath = mp3; break; }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (audioPath == null || !Files.exists(audioPath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            String contentType = Files.probeContentType(audioPath);
            if (contentType == null) contentType = "audio/mpeg";

            FileSystemResource resource = new FileSystemResource(audioPath);
            long contentLength = Files.size(audioPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(contentLength)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取本地歌曲封面图（支持子目录查找）
     * GET /api/songs/cover/{songId}
     */
    @GetMapping("/cover/{songId}")
    public ResponseEntity<Resource> getCover(@PathVariable String songId) {
        Path basePath = Paths.get(storePath);
        Path coverPath = null;

        try {
            List<Path> subDirs = Files.list(basePath)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            for (Path dir : subDirs) {
                Path jpg = dir.resolve(songId + ".jpg");
                if (Files.exists(jpg)) { coverPath = jpg; break; }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (coverPath == null || !Files.exists(coverPath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            String contentType = Files.probeContentType(coverPath);
            if (contentType == null) contentType = "image/jpeg";

            FileSystemResource resource = new FileSystemResource(coverPath);
            long contentLength = Files.size(coverPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(contentLength)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取本地歌曲歌词（LRC格式，支持子目录查找）
     * GET /api/songs/lyrics/{songId}
     */
    @GetMapping(value = "/lyrics/{songId}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public ResponseEntity<String> getLyrics(@PathVariable String songId) {
        Path basePath = Paths.get(storePath);
        Path lrcPath = null;

        try {
            List<Path> subDirs = Files.list(basePath)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            for (Path dir : subDirs) {
                Path lrc = dir.resolve(songId + ".lrc");
                if (Files.exists(lrc)) { lrcPath = lrc; break; }
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (lrcPath == null || !Files.exists(lrcPath)) {
            return ResponseEntity.notFound().build();
        }

        try {
            String lrcContent = Files.readString(lrcPath, StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain;charset=UTF-8"))
                    .body(lrcContent);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 触发本地音乐库重新扫描
     * POST /api/songs/scan
     */
    @PostMapping("/scan")
    public Map<String, Object> scanLocal() {
        // 本地歌曲列表从 metadata.json 实时读取，无需额外刷新
        return Map.of("status", "ok", "message", "扫描完成");
    }
}
