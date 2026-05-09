package com.crawl.qeecc.controller;

import com.crawl.qeecc.service.QeeccCrawlerService;
import com.crawl.qeecc.service.DanceMusicCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音频爬虫控制器
 * 提供音频爬虫相关的REST API接口
 */
@RestController
@RequestMapping("/api/audio")
public class AudioCrawlController {

    @Autowired
    private QeeccCrawlerService qeeccCrawlerService;

    @Autowired
    private DanceMusicCrawler danceMusicCrawler;

    /**
     * 获取DJ舞曲分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<List<DanceMusicCrawler.DanceMusicCategory>> getDanceMusicCategories() {
        try {
            List<DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 从指定分类获取音频列表
     */
    @GetMapping("/category/{categoryUrl}/tracks")
    public ResponseEntity<List<DanceMusicCrawler.AudioTrack>> getAudioTracks(@PathVariable String categoryUrl) {
        try {
            // 解码URL
            String decodedUrl = java.net.URLDecoder.decode(categoryUrl, "UTF-8");
            List<DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(decodedUrl);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 执行完整的爬虫流程
     */
    @PostMapping("/crawl/full")
    public ResponseEntity<QeeccCrawlerService.CrawlResult> startFullCrawl() {
        try {
            QeeccCrawlerService.CrawlResult result = qeeccCrawlerService.executeCrawlProcess();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            QeeccCrawlerService.CrawlResult errorResult = new QeeccCrawlerService.CrawlResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage("爬虫执行失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResult);
        }
    }

    /**
     * 获取单个音频样本（用于测试）
     */
    @PostMapping("/crawl/sample")
    public ResponseEntity<QeeccCrawlerService.SingleAudioResult> getSampleAudio() {
        try {
            QeeccCrawlerService.SingleAudioResult result = qeeccCrawlerService.getCoolMusicSample();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            QeeccCrawlerService.SingleAudioResult errorResult = new QeeccCrawlerService.SingleAudioResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage("获取音频样本失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResult);
        }
    }

    /**
     * 测试连接
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Audio Crawler Service is running");
    }
}