package com.crawl.qeecc.tool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SongSearchFunction 单元测试
 * 验证歌曲查询工具能否正常工作
 */
@SpringBootTest
class SongSearchFunctionTest {

    @Autowired
    private SongSearchFunction songSearchFunction;

    @Test
    void testSearchJiangNan() {
        System.out.println("========================================");
        System.out.println("🎵 测试查询歌曲: 江南");
        System.out.println("========================================");

        // 构造请求
        SongSearchFunction.Request request = new SongSearchFunction.Request("江南");

        // 调用工具
        SongSearchFunction.Response response = songSearchFunction.apply(request);

        // 验证结果
        assertNotNull(response, "响应不应为空");
        assertNotNull(response.result(), "结果不应为空");

        System.out.println("✅ 查询完成");
        System.out.println("📋 结果: " + response.result());

        // 无论找到与否，结果中都应该包含"江南"
        assertTrue(
                response.result().contains("江南"),
                "结果中应包含查询关键词'江南'"
        );
    }

    @Test
    void testSearchNotExistSong() {
        System.out.println("========================================");
        System.out.println("🎵 测试查询不存在的歌曲: zzzxxx123456");
        System.out.println("========================================");

        SongSearchFunction.Request request = new SongSearchFunction.Request("zzzxxx123456");
        SongSearchFunction.Response response = songSearchFunction.apply(request);

        assertNotNull(response);
        assertNotNull(response.result());

        System.out.println("📋 结果: " + response.result());

        // 应返回未找到
        assertTrue(
                response.result().contains("未找到"),
                "对于不存在的歌曲，结果中应提示'未找到'"
        );
    }
}
