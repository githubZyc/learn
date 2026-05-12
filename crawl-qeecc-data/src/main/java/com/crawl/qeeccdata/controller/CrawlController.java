package com.crawl.qeeccdata.controller;

import com.crawl.qeeccdata.service.QeeccDataCrawler;
import com.crawl.qeeccdata.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 爬取控制 API
 * 触发爬取、查看进度、列出可用版块、搜索歌曲、下载歌曲
 */
@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    private final QeeccDataCrawler crawler;
    private final SearchService searchService;

    public CrawlController(QeeccDataCrawler crawler, SearchService searchService) {
        this.crawler = crawler;
        this.searchService = searchService;
    }

    /**
     * 爬取指定版块
     * GET /api/crawl/section?name=新歌榜
     */
    @GetMapping("/section")
    public Map<String, Object> crawlSection(@RequestParam String name) {
        return crawler.crawlSection(name);
    }

    /**
     * 爬取所有版块
     * POST /api/crawl/all
     */
    @GetMapping("/all")
    public Map<String, Object> crawlAll() {
        return crawler.crawlAll();
    }

    /**
     * 查看当前爬取进度
     * GET /api/crawl/progress
     */
    @GetMapping("/progress")
    public Map<String, Object> getProgress() {
        return crawler.getProgress();
    }

    /**
     * 列出所有可用版块
     * GET /api/crawl/sections
     */
    @GetMapping("/sections")
    public List<Map<String, String>> getSections() {
        return crawler.getAvailableSections();
    }

    // ==================== 搜索 & 下载 ====================

    /**
     * 搜索歌曲
     * GET /api/crawl/search?keyword=江南
     */
    @GetMapping("/search")
    public List<SearchService.SearchSongResult> search(@RequestParam String keyword) {
        return searchService.search(keyword);
    }

    /**
     * 下载指定歌曲
     * POST /api/crawl/download?songId=c2R3eW4
     */
    @PostMapping("/download")
    public SearchService.DownloadResult download(@RequestParam String songId) {
        return searchService.download(songId);
    }

    // ==================== 歌词修复 ====================

    /**
     * 批量修复已下载歌曲的歌词
     * POST /api/crawl/repair-lyrics
     * 扫描所有 metadata.json, 对缺少歌词的已下载歌曲批量下载 .lrc 文件
     */
    @GetMapping("/repair-lyrics")
    public Map<String, Object> repairLyrics() {
        System.out.println("[Controller] 收到批量修复歌词请求");
        Map<String, Object> result = crawler.repairLyrics();
        System.out.println("[Controller] 批量修复歌词完成: " + result);
        return result;
    }

    /**
     * 单曲补歌词
     * GET /api/crawl/fetch-lyrics?songId=eWNkbnNkbnh3
     */
    @GetMapping("/fetch-lyrics")
    public Map<String, Object> fetchLyrics(@RequestParam String songId) {
        System.out.println("[Controller] 收到单曲补歌词请求: songId=" + songId);
        Map<String, Object> result = crawler.fetchLyrics(songId);
        System.out.println("[Controller] 单曲补歌词完成: " + result);
        return result;
    }
}
