package com.crawl.qeecc.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 热门歌曲缓存服务
 * 启动时预加载热门歌曲列表（搜索结果），供前端直接展示
 * 播放链接因有过期时间，由前端按需请求
 */
@Service
public class SongCacheService {

    private final QeeccSearchService searchService;

    /** 预加载的热门歌曲列表（线程安全） */
    private final CopyOnWriteArrayList<CachedSong> hotSongs = new CopyOnWriteArrayList<>();

    /** 热门搜索关键词 */
    private static final String[] HOT_KEYWORDS = {
            "江南", "晴天", "海阔天空", "成都", "偏爱", "泡沫", "十年", "入画江南"
    };

    /** 每个关键词取前 N 首歌 */
    private static final int MAX_PER_KEYWORD = 3;

    public SongCacheService(QeeccSearchService searchService) {
        this.searchService = searchService;
    }

    @PostConstruct
    public void init() {
        // 异步预加载，避免阻塞启动
        Thread.ofVirtual().start(() -> {
            loadHotSongs();
        });
    }

    private void loadHotSongs() {
        System.out.println("[Cache] 开始预加载热门歌曲...");
        for (String keyword : HOT_KEYWORDS) {
            try {
                List<QeeccSearchService.SearchResult> results = searchService.searchSongs(keyword);
                int count = 0;
                for (QeeccSearchService.SearchResult sr : results) {
                    if (count >= MAX_PER_KEYWORD) break;
                    // 去重：按 songId 去重
                    boolean exists = hotSongs.stream().anyMatch(s -> s.songId().equals(sr.songId()));
                    if (!exists) {
                        hotSongs.add(new CachedSong(
                                sr.songId(),
                                sr.songName(),
                                sr.singer(),
                                sr.href(),
                                keyword  // 作为来源标签
                        ));
                        count++;
                    }
                }
            } catch (Exception e) {
                System.out.println("[Cache] 加载关键词 '" + keyword + "' 失败: " + e.getMessage());
            }
        }
        System.out.println("[Cache] 预加载完成，共 " + hotSongs.size() + " 首热门歌曲");
    }

    /**
     * 获取热门歌曲列表
     */
    public List<CachedSong> getHotSongs() {
        return new ArrayList<>(hotSongs);
    }

    /**
     * 搜索歌曲（实时搜索，不走缓存）
     */
    public List<CachedSong> search(String keyword) {
        List<QeeccSearchService.SearchResult> results = searchService.searchSongs(keyword);
        return results.stream()
                .map(sr -> new CachedSong(sr.songId(), sr.songName(), sr.singer(), sr.href(), keyword))
                .toList();
    }

    /**
     * 获取播放链接（实时获取，因链接有过期时间）
     */
    public QeeccSearchService.PlayResult getPlayUrl(String songId) {
        return searchService.getPlayUrl(songId);
    }

    /**
     * 缓存的歌曲数据
     */
    public record CachedSong(
            String songId,
            String songName,
            String singer,
            String href,
            String tag
    ) {}
}
