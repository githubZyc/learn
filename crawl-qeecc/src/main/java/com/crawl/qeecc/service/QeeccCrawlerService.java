package com.crawl.qeecc.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Qeecc网站爬虫服务
 * 协调整个爬虫流程：发现DJ舞曲分类 -> 获取音频列表 -> 下载音频文件
 */
@Service
public class QeeccCrawlerService {

    @Autowired
    private DanceMusicCrawler danceMusicCrawler;

    @Autowired
    private AudioDownloadService audioDownloadService;

    /**
     * 执行完整的爬虫流程
     * 1. 查找DJ舞曲分类
     * 2. 从每个分类获取音频轨道
     * 3. 下载音频文件
     */
    public CrawlResult executeCrawlProcess() {
        CrawlResult result = new CrawlResult();
        
        try {

            // 步骤1: 查找DJ舞曲分类
            List<DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
            result.setCategoriesFound(categories.size());
            
            if (CollUtil.isEmpty(categories)) {
                result.setSuccess(false);
                result.setErrorMessage("未找到任何DJ舞曲分类");
                return result;
            }
            

            // 步骤2: 从每个分类获取音频轨道
            int totalTracks = 0;
            int totalDownloads = 0;
            
            for (DanceMusicCrawler.DanceMusicCategory category : categories) {

                List<DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(category.getUrl());
                totalTracks += tracks.size();
                

                // 步骤3: 下载音频文件
                for (DanceMusicCrawler.AudioTrack track : tracks) {

                    AudioDownloadService.DownloadResult downloadResult = audioDownloadService.downloadAudio(
                            track.getAudioUrl(), 
                            generateSafeFileName(track.getTitle())
                    );
                    
                    if (downloadResult.isSuccess()) {
                        totalDownloads++;
                    } else {
                    }
                    
                    // 添加延迟以减少对服务器的压力
                    try {
                        Thread.sleep(1000); // 休眠1秒
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // 检查是否被中断
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
            
            result.setTracksFound(totalTracks);
            result.setFilesDownloaded(totalDownloads);
            result.setSuccess(true);
            

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("爬虫流程执行失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 获取单个音频文件
     * 用于测试目的，获取一个DJ舞曲音频
     */
    public SingleAudioResult getCoolMusicSample() {
        SingleAudioResult result = new SingleAudioResult();
        
        try {

            // 查找DJ舞曲分类
            List<DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();
            
            if (CollUtil.isEmpty(categories)) {
                result.setSuccess(false);
                result.setErrorMessage("未找到任何DJ舞曲分类");
                return result;
            }
            
            // 选择第一个分类
            DanceMusicCrawler.DanceMusicCategory firstCategory = categories.get(0);

            // 从该分类获取音频轨道
            List<DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(firstCategory.getUrl());
            
            if (CollUtil.isEmpty(tracks)) {
                result.setSuccess(false);
                result.setErrorMessage("在分类 '" + firstCategory.getName() + "' 中未找到音频轨道");
                return result;
            }
            
            // 选择第一个音频进行下载
            DanceMusicCrawler.AudioTrack firstTrack = tracks.get(0);

            // 下载音频文件
            AudioDownloadService.DownloadResult downloadResult = audioDownloadService.downloadAudio(
                    firstTrack.getAudioUrl(),
                    generateSafeFileName(firstTrack.getTitle())
            );
            
            if (downloadResult.isSuccess()) {
                result.setSuccess(true);
                result.setAudioTitle(firstTrack.getTitle());
                result.setAudioUrl(firstTrack.getAudioUrl());
                result.setDownloadPath(downloadResult.getFilePath());
                result.setCategoryName(firstCategory.getName());
                
            } else {
                result.setSuccess(false);
                result.setErrorMessage("音频下载失败: " + downloadResult.getErrorMessage());
            }
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("获取音频样本失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 生成安全的文件名（移除特殊字符）
     */
    private String generateSafeFileName(String originalName) {
        if (StrUtil.isBlank(originalName)) {
            return null;
        }
        
        // 替换不安全的字符
        String safeName = originalName.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // 限制长度
        if (safeName.length() > 100) {
            safeName = safeName.substring(0, 100);
        }
        
        // 确保有音频扩展名
        if (!safeName.toLowerCase().matches(".*\\.(mp3|wav|ogg|m4a|aac|flac)$")) {
            safeName += ".mp3";
        }
        
        return safeName;
    }

    /**
     * 爬虫结果类
     */
    public static class CrawlResult {
        private boolean success;
        private String errorMessage;
        private int categoriesFound;
        private int tracksFound;
        private int filesDownloaded;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public int getCategoriesFound() { return categoriesFound; }
        public void setCategoriesFound(int categoriesFound) { this.categoriesFound = categoriesFound; }

        public int getTracksFound() { return tracksFound; }
        public void setTracksFound(int tracksFound) { this.tracksFound = tracksFound; }

        public int getFilesDownloaded() { return filesDownloaded; }
        public void setFilesDownloaded(int filesDownloaded) { this.filesDownloaded = filesDownloaded; }

        @Override
        public String toString() {
            return "CrawlResult{" +
                    "success=" + success +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", categoriesFound=" + categoriesFound +
                    ", tracksFound=" + tracksFound +
                    ", filesDownloaded=" + filesDownloaded +
                    '}';
        }
    }

    /**
     * 单个音频结果类
     */
    public static class SingleAudioResult {
        private boolean success;
        private String errorMessage;
        private String audioTitle;
        private String audioUrl;
        private String categoryName;
        private String downloadPath;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public String getAudioTitle() { return audioTitle; }
        public void setAudioTitle(String audioTitle) { this.audioTitle = audioTitle; }

        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getDownloadPath() { return downloadPath; }
        public void setDownloadPath(String downloadPath) { this.downloadPath = downloadPath; }

        @Override
        public String toString() {
            return "SingleAudioResult{" +
                    "success=" + success +
                    ", audioTitle='" + audioTitle + '\'' +
                    ", audioUrl='" + audioUrl + '\'' +
                    ", categoryName='" + categoryName + '\'' +
                    ", downloadPath='" + downloadPath + '\'' +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}