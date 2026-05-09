package com.crawl.qeecc;

import com.crawl.qeecc.service.QeeccCrawlerService;
import com.crawl.qeecc.service.DanceMusicCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CrawlerTest {

    @Autowired
    private QeeccCrawlerService qeeccCrawlerService;

    @Autowired
    private DanceMusicCrawler danceMusicCrawler;

    /**
     * 测试获取单个音频样本
     */
    @Test
    public void testGetSampleAudio() {
        System.out.println("开始测试获取单个音频样本...");
        
        QeeccCrawlerService.SingleAudioResult result = qeeccCrawlerService.getCoolMusicSample();
        
        if (result.isSuccess()) {
            System.out.println("✅ 获取音频样本成功!");
            System.out.println("🎵 音频标题: " + result.getAudioTitle());
            System.out.println("🔗 音频URL: " + result.getAudioUrl());
            System.out.println("📁 分类名称: " + result.getCategoryName());
            System.out.println("💾 下载路径: " + result.getDownloadPath());
        } else {
            System.out.println("❌ 获取音频样本失败: " + result.getErrorMessage());
        }
    }

    /**
     * 测试查找DJ舞曲分类
     */
    @Test
    public void testFindDanceMusicCategories() {
        System.out.println("开始测试查找DJ舞曲分类...");
        
        java.util.List<com.crawl.qeecc.service.DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
        
        System.out.println("找到 " + categories.size() + " 个DJ舞曲分类:");
        for (com.crawl.qeecc.service.DanceMusicCrawler.DanceMusicCategory category : categories) {
            System.out.println("- " + category.getName() + " -> " + category.getUrl());
        }
    }
}