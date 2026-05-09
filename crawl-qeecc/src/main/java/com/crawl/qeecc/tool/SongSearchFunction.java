package com.crawl.qeecc.tool;

import com.crawl.qeecc.service.QeeccSearchService;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * 能力层 - 歌曲搜索工具
 * 利用 qeecc.com 站内搜索接口，搜索歌曲并获取播放链接
 * Agent 编排层通过 LLM 自动决策调用此工具
 */
@Component
@Description("在 qeecc.com 网站搜索指定歌曲，返回歌曲是否存在及播放链接")
public class SongSearchFunction implements Function<SongSearchFunction.Request, SongSearchFunction.Response> {

    private final QeeccSearchService searchService;

    public SongSearchFunction(QeeccSearchService searchService) {
        this.searchService = searchService;
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
            QeeccSearchService.SearchWithPlayResult result = searchService.searchAndGetPlayUrl(songName);

            if (!result.found() || result.searchResults().isEmpty()) {
                String notFound = "未找到歌曲: " + songName;
                System.out.println("[Tool] " + notFound);
                return new Response(notFound);
            }

            // 构建搜索结果
            StringBuilder sb = new StringBuilder();
            sb.append("找到 ").append(result.searchResults().size()).append(" 首相关歌曲:\n");

            // 列出前 5 首
            List<QeeccSearchService.SearchResult> top = result.searchResults().stream().limit(5).toList();
            for (int i = 0; i < top.size(); i++) {
                QeeccSearchService.SearchResult sr = top.get(i);
                sb.append(i + 1).append(". ").append(sr.singer()).append(" - ").append(sr.songName());
                sb.append(" (ID: ").append(sr.songId()).append(")\n");
            }

            // 附加第一首的播放链接
            if (result.playResult() != null) {
                sb.append("\n第一首播放链接: ").append(result.playResult().mp3Url());
            }

            System.out.println("[Tool] 搜索完成，找到 " + result.searchResults().size() + " 首");
            return new Response(sb.toString());

        } catch (Exception e) {
            String error = "搜索失败: " + e.getMessage();
            System.out.println("[Tool] " + error);
            return new Response(error);
        }
    }
}
