package com.crawl.qeeccdata.model;

/**
 * 爬取歌曲数据模型 - 三层共用
 * Layer1(索引) → Layer2(元数据) → Layer3(下载)
 */
public record CrawlSong(
        String songId,          // 歌曲ID, 如 eWNkbnNkbnh3
        String songName,        // 歌曲名
        String singer,          // 歌手名
        String source,          // 来源版块: 新歌榜/TOP榜单/DJ舞曲/歌手/歌单/电台/高清MV
        String sourceDetail,    // 来源详情: 如 "歌手/林俊杰" 或 "歌单/伤感热歌"
        String href,            // 详情页路径 /song/xxx.html
        String fullText,        // 原始文本 "林俊杰《江南》[Mp3]"

        // Layer2 填充
        String mp3Url,          // MP3直链(有过期时间)
        String pic,             // 封面图URL
        int lkid,               // 歌词ID

        // Layer3 填充
        String localAudioFile,  // 本地音频文件名: songId.m4a
        String localPicFile,    // 本地封面文件名: songId.jpg
        long fileSize,          // 文件大小(字节)
        String downloadTime     // 下载完成时间
) {
    /**
     * Layer1 构造: 仅索引信息
     */
    public static CrawlSong forIndex(String songId, String songName, String singer,
                                     String source, String sourceDetail, String href, String fullText) {
        return new CrawlSong(songId, songName, singer, source, sourceDetail, href, fullText,
                null, null, 0, null, null, 0, null);
    }

    /**
     * Layer2 填充: 加入 Play API 结果
     */
    public CrawlSong withPlayResult(String mp3Url, String pic, int lkid) {
        return new CrawlSong(songId, songName, singer, source, sourceDetail, href, fullText,
                mp3Url, pic, lkid, localAudioFile, localPicFile, fileSize, downloadTime);
    }

    /**
     * Layer3 填充: 加入下载结果
     */
    public CrawlSong withDownloadResult(String localAudioFile, String localPicFile,
                                        long fileSize, String downloadTime) {
        return new CrawlSong(songId, songName, singer, source, sourceDetail, href, fullText,
                mp3Url, pic, lkid, localAudioFile, localPicFile, fileSize, downloadTime);
    }

    /**
     * 是否已获取播放链接
     */
    public boolean hasPlayResult() {
        return mp3Url != null && !mp3Url.isEmpty();
    }

    /**
     * 清除 Play API 结果 (用于 403 重试时重新获取)
     */
    public CrawlSong withoutPlayResult() {
        return new CrawlSong(songId, songName, singer, source, sourceDetail, href, fullText,
                null, null, 0, localAudioFile, localPicFile, fileSize, downloadTime);
    }

    /**
     * 是否已下载
     */
    public boolean isDownloaded() {
        return localAudioFile != null && !localAudioFile.isEmpty();
    }
}
