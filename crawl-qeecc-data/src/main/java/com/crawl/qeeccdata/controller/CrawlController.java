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
    @PostMapping("/all")
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
}
