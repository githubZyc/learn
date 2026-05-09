package com.crawl.qeecc;

import com.crawl.qeecc.service.DanceMusicCrawler;
import com.crawl.qeecc.service.QeeccCrawlerService;
import com.crawl.qeecc.util.SiteStructureAnalyzer;
import com.crawl.qeecc.util.WebPageFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestCrawler implements CommandLineRunner {

    @Autowired
    private DanceMusicCrawler danceMusicCrawler;

    @Autowired
    private QeeccCrawlerService qeeccCrawlerService;

    @Autowired
    private WebPageFetcher webPageFetcher;

    @Autowired
    private SiteStructureAnalyzer siteStructureAnalyzer;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 开始测试爬虫功能 ===");
        
        // 测试1: 获取DJ舞曲分类
        System.out.println("\n1. 获取DJ舞曲分类:");
        java.util.List<com.crawl.qeecc.service.DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
        System.out.println("找到 " + categories.size() + " 个分类:");
        for (int i = 0; i < Math.min(categories.size(), 5); i++) { // 只显示前5个
            com.crawl.qeecc.service.DanceMusicCrawler.DanceMusicCategory category = categories.get(i);
            System.out.println("  - " + category.getName() + " -> " + category.getUrl());
        }
        
        if (!categories.isEmpty()) {
            // 测试2: 从第一个分类获取音频
            com.crawl.qeecc.service.DanceMusicCrawler.DanceMusicCategory firstCategory = categories.get(0);
            System.out.println("\n2. 从分类 '" + firstCategory.getName() + "' 获取音频:");
            java.util.List<com.crawl.qeecc.service.DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(firstCategory.getUrl());
            System.out.println("找到 " + tracks.size() + " 个音频轨道:");
            for (int i = 0; i < Math.min(tracks.size(), 5); i++) { // 只显示前5个
                com.crawl.qeecc.service.DanceMusicCrawler.AudioTrack track = tracks.get(i);
                System.out.println("  - 标题: " + track.getTitle());
                System.out.println("    音频URL: " + track.getAudioUrl());
                System.out.println("    来源: " + track.getSourceType());
                System.out.println();
            }
        }
        
        System.out.println("=== 爬虫测试完成 ===");
    }
}