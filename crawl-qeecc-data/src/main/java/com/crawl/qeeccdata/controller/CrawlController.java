package com.crawl.qeeccdata.controller;

import com.crawl.qeeccdata.service.QeeccDataCrawler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 爬取控制 API
 * 触发爬取、查看进度、列出可用版块
 */
@RestController
@RequestMapping("/api/crawl")
public class CrawlController {

    private final QeeccDataCrawler crawler;

    public CrawlController(QeeccDataCrawler crawler) {
        this.crawler = crawler;
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
}
