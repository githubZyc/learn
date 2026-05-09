package com.crawl.qeecc.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 音频下载服务
 * 负责下载音频文件并保存到本地
 */
@Service
public class AudioDownloadService {

    @Value("${audio.download.path:./downloads/audio}")
    private String downloadPath;

    @Value("${audio.download.timeout:30000}")
    private int timeout;

    /**
     * 下载音频文件
     * @param audioUrl 音频文件URL
     * @param fileName 文件名（可选，如果为空则自动生成）
     * @return 下载结果
     */
    public DownloadResult downloadAudio(String audioUrl, String fileName) {
        DownloadResult result = new DownloadResult();
        result.setAudioUrl(audioUrl);

        try {
            // 验证URL
            if (StrUtil.isBlank(audioUrl)) {
                result.setSuccess(false);
                result.setErrorMessage("音频URL不能为空");
                return result;
            }

            // 创建下载目录
            FileUtil.mkdir(downloadPath);

            // 如果没有指定文件名，则生成唯一文件名
            if (StrUtil.isBlank(fileName)) {
                fileName = generateFileName(audioUrl);
            }

            // 构建完整路径
            String fullPath = Paths.get(downloadPath, fileName).toString();

            // 下载文件
            System.out.println("开始下载音频文件: " + audioUrl + " -> " + fullPath);
            byte[] content = HttpUtil.downloadBytes(audioUrl);
            
            if (content != null && content.length > 0) {
                FileUtil.writeBytes(content, fullPath);
                
                File downloadedFile = new File(fullPath);
                result.setSuccess(true);
                result.setFilePath(fullPath);
                result.setFileSize(downloadedFile.length());
                result.setFileName(fileName);

                System.out.println("音频文件下载成功: " + fullPath + ", 大小: " + downloadedFile.length() + " bytes");
            } else {
                result.setSuccess(false);
                result.setErrorMessage("下载的文件内容为空");
            }
        } catch (Exception e) {
            System.out.println("下载音频文件失败: " + audioUrl + ", 错误: " + e.getMessage());
            result.setSuccess(false);
            result.setErrorMessage("下载失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 从URL生成合适的文件名
     */
    private String generateFileName(String audioUrl) {
        // 从URL中提取原始文件名
        String originalName = getFileNameFromUrl(audioUrl);
        
        if (StrUtil.isNotBlank(originalName)) {
            // 验证文件扩展名
            if (isValidAudioExtension(originalName)) {
                return originalName;
            }
        }

        // 如果无法从URL获取有效文件名，则生成一个唯一的文件名
        String extension = getExtensionFromUrl(audioUrl);
        if (StrUtil.isBlank(extension) || !isValidAudioExtension("." + extension)) {
            extension = "mp3"; // 默认使用mp3
        }

        return "audio_" + IdUtil.simpleUUID() + "." + extension;
    }

    /**
     * 从URL中提取文件名
     */
    private String getFileNameFromUrl(String url) {
        try {
            String[] parts = url.split("/");
            String fileName = parts[parts.length - 1];
            
            // 移除查询参数
            int queryIndex = fileName.indexOf('?');
            if (queryIndex >= 0) {
                fileName = fileName.substring(0, queryIndex);
            }
            
            return fileName;
        } catch (Exception e) {
            System.out.println("从URL中提取文件名失败: " + url + ", 错误: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从URL中获取扩展名
     */
    private String getExtensionFromUrl(String url) {
        try {
            String fileName = getFileNameFromUrl(url);
            if (StrUtil.isBlank(fileName)) {
                return null;
            }
            
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                return fileName.substring(lastDotIndex + 1);
            }
            
            return null;
        } catch (Exception e) {
            System.out.println("从URL中获取扩展名失败: " + url + ", 错误: " + e.getMessage());
            return null;
        }
    }

    /**
     * 验证是否为有效的音频扩展名
     */
    private boolean isValidAudioExtension(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return false;
        }

        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".mp3") || 
               lowerFileName.endsWith(".wav") || 
               lowerFileName.endsWith(".ogg") || 
               lowerFileName.endsWith(".m4a") ||
               lowerFileName.endsWith(".aac") ||
               lowerFileName.endsWith(".flac") ||
               lowerFileName.endsWith(".wma");
    }

    /**
     * 下载结果类
     */
    public static class DownloadResult {
        private String audioUrl;      // 原始音频URL
        private boolean success;      // 是否成功
        private String filePath;      // 本地文件路径
        private String fileName;      // 文件名
        private long fileSize;        // 文件大小（字节）
        private String errorMessage;  // 错误消息

        // Getters and Setters
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        @Override
        public String toString() {
            return "DownloadResult{" +
                    "audioUrl='" + audioUrl + '\'' +
                    ", success=" + success +
                    ", filePath='" + filePath + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", fileSize=" + fileSize +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }
}