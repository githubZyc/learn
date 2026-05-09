package com.crawl.qeecc.tool;

import com.crawl.qeecc.service.DanceMusicCrawler;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * 能力层 - 歌曲搜索工具
 * 复用 DanceMusicCrawler 爬虫能力，封装为 Function Calling 工具
 * Agent 编排层通过 LLM 自动决策调用此工具
 */
@Component
@Description("在 qeecc.com 网站搜索指定歌曲是否存在，输入歌曲名称，返回搜索结果")
public class SongSearchFunction implements Function<SongSearchFunction.Request, SongSearchFunction.Response> {

    private final DanceMusicCrawler danceMusicCrawler;

    public SongSearchFunction(DanceMusicCrawler danceMusicCrawler) {
        this.danceMusicCrawler = danceMusicCrawler;
    }

    /**
     * Function 输入参数
     * Spring AI 会自动生成 JSON Schema 供 LLM 理解参数结构
     */
    public record Request(String songName) {}

    /**
     * Function 输出结果
     */
    public record Response(String result) {}

    @Override
    public Response apply(Request request) {
        String songName = request.songName();
        System.out.println("[Tool] 调用歌曲搜索工具，关键词: " + songName);

        try {
            // 获取所有分类
            List<DanceMusicCrawler.DanceMusicCategory> categories = danceMusicCrawler.findDanceMusicCategories();

            // 遍历所有分类查找歌曲
            for (DanceMusicCrawler.DanceMusicCategory category : categories) {
                List<DanceMusicCrawler.AudioTrack> tracks = danceMusicCrawler.getAudioTracksFromCategory(category.getUrl());
                for (DanceMusicCrawler.AudioTrack track : tracks) {
                    String title = track.getTitle();
                    if (title != null && title.toLowerCase().contains(songName.toLowerCase())) {
                        String found = String.format("找到歌曲: %s | 分类: %s | URL: %s",
                                title, category.getName(), track.getAudioUrl());
                        System.out.println("[Tool] " + found);
                        return new Response(found);
                    }
                }
            }

            String notFound = "未找到歌曲: " + songName;
            System.out.println("[Tool] " + notFound);
            return new Response(notFound);

        } catch (Exception e) {
            String error = "搜索失败: " + e.getMessage();
            System.out.println("[Tool] " + error);
            return new Response(error);
        }
    }
}
