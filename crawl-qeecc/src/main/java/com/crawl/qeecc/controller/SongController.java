package com.crawl.qeecc.controller;

import com.crawl.qeecc.service.QeeccSearchService;
import com.crawl.qeecc.service.SongCacheService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 歌曲数据 API
 * 提供热门歌曲列表、搜索、播放链接获取接口
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongCacheService songCacheService;

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
}
