package com.crawl.qeecc.tool;

import com.crawl.qeecc.service.QeeccSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 歌曲搜索功能测试
 * 验证站内搜索 + 播放链接获取的完整链路
 */
@SpringBootTest
class SongSearchFunctionTest {

    @Autowired
    private SongSearchFunction songSearchFunction;

    @Autowired
    private QeeccSearchService searchService;

    @Test
    void testSearchJiangNan() {
        System.out.println("========================================");
        System.out.println("测试查询歌曲: 江南");
        System.out.println("========================================");

        SongSearchFunction.Request request = new SongSearchFunction.Request("江南");
        SongSearchFunction.Response response = songSearchFunction.apply(request);

        assertNotNull(response, "响应不应为空");
        assertNotNull(response.result(), "结果不应为空");

        System.out.println("结果: " + response.result());

        assertTrue(
                response.result().contains("江南"),
                "结果中应包含查询关键词'江南'"
        );
    }

    @Test
    void testSearchNotExistSong() {
        System.out.println("========================================");
        System.out.println("测试查询不存在的歌曲: zzzxxx123456");
        System.out.println("========================================");

        SongSearchFunction.Request request = new SongSearchFunction.Request("zzzxxx123456");
        SongSearchFunction.Response response = songSearchFunction.apply(request);

        assertNotNull(response);
        assertNotNull(response.result());

        System.out.println("结果: " + response.result());

        assertTrue(
                response.result().contains("未找到"),
                "对于不存在的歌曲，结果中应提示'未找到'"
        );
    }

    @Test
    void testSearchAndGetPlayUrl() {
        System.out.println("========================================");
        System.out.println("测试搜索+获取播放链接: 江南");
        System.out.println("========================================");

        QeeccSearchService.SearchWithPlayResult result = searchService.searchAndGetPlayUrl("江南");

        assertNotNull(result, "结果不应为空");
        assertTrue(result.found(), "应该找到歌曲");
        assertFalse(result.searchResults().isEmpty(), "搜索结果不应为空");

        System.out.println("搜索结果数量: " + result.searchResults().size());
        for (var sr : result.searchResults()) {
            System.out.println("  - " + sr.singer() + "《" + sr.songName() + "》 ID=" + sr.songId());
        }

        if (result.playResult() != null) {
            System.out.println("播放链接: " + result.playResult().mp3Url());
            assertFalse(result.playResult().mp3Url().isEmpty(), "播放链接不应为空");
        }
    }
}
