package com.crawl.qeecc;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试访问播放接口的Java类
 * 模拟JavaScript中player函数的功能
 */
@SpringBootTest
public class CrawlPlayApiTest {

    private static final String BASE_URL = "http://www.qeecc.com/list/new.html";
    private static final String PLAY_API_PATH = "/js/play.php";
    
    /**
     * 测试播放接口调用
     * 对应JavaScript中player(type, id)函数的逻辑
     */
    @Test
    public void testPlayApi() {
        // 对应JavaScript中的player(type, id)参数
        String testType = "audio"; // 示例类型
        String testId = "1";       // 示例ID
        
        // 构建请求参数 - 对应JavaScript中的{id:""+id+"",type:""+type+""}
        Map<String, String> params = new HashMap<>();
        params.put("id", testId);
        params.put("type", testType);
        
        try {
            // 使用Hutool发送POST请求，与项目中其他地方使用的HTTP客户端一致
            String response = HttpRequest.post(BASE_URL + PLAY_API_PATH)
                    .form(String.valueOf(params))  // 表单参数
                    .header("Cookie", "server_name_session=f5b3f0607f1165efc92396ff69a90dee; Hm_tf_6rdiytabw7z=1768987949; Hm_lvt_6rdiytabw7z=1768987949; Hm_lvt_00c97102af6d427421274b7ae48b4c2c=1768987983; HMACCOUNT=78252BD20555FA89; Hm_lpvt_00c97102af6d427421274b7ae48b4c2c=1769065975; Hm_lpvt_6rdiytabw7z=1769066678; bb5c6016a611845824b1a3fc9f8e1b5a=3e7fa294c33530f1c2e875020782ab9a")
                    .timeout(10000)
                    .execute()
                    .body();
            
            System.out.println("接口调用成功，响应数据：" + response);
            
            // 解析返回的JSON数据，对应JavaScript中success回调函数处理的data对象
            if (JSONUtil.isJson(response)) {
                JSONObject jsonResponse = JSONUtil.parseObj(response);
                
                // 检查响应中是否包含预期的字段（对应JavaScript中的data.url、data.pic、data.lkid）
                String url = jsonResponse.getStr("url");
                String pic = jsonResponse.getStr("pic");
                String lkid = jsonResponse.getStr("lkid");
                
                System.out.println("音频URL: " + url);
                System.out.println("封面图片: " + pic);
                System.out.println("歌词ID: " + lkid);
                
                if (url != null && !url.isEmpty()) {
                    System.out.println("音频播放地址获取成功");
                } else {
                    System.out.println("警告：响应中未找到音频URL");
                }
            } else {
                System.out.println("响应不是有效的JSON格式");
            }
            
        } catch (Exception e) {
            System.err.println("接口调用失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试使用HttpRequest的另一种方式
     */
    @Test
    public void testPlayApiWithHttpRequest() {
        String testType = "music";
        String testId = "eXdjY3hnc3lr";
        
        try (HttpResponse response = HttpRequest.post(BASE_URL + PLAY_API_PATH)
                .form("id", testId)  // 表单参数
                .form("type", testType)
                .header("Cookie", "server_name_session=f5b3f0607f1165efc92396ff69a90dee; Hm_tf_6rdiytabw7z=1768987949; Hm_lvt_6rdiytabw7z=1768987949; Hm_lvt_00c97102af6d427421274b7ae48b4c2c=1768987983; HMACCOUNT=78252BD20555FA89; Hm_lpvt_00c97102af6d427421274b7ae48b4c2c=1769065975; Hm_lpvt_6rdiytabw7z=1769066678; bb5c6016a611845824b1a3fc9f8e1b5a=3e7fa294c33530f1c2e875020782ab9a")
                .timeout(10000)
                .execute()) {
            
            String responseBody = response.body();
            System.out.println("使用HttpRequest接口调用成功，响应数据：" + responseBody);
            
            if (JSONUtil.isJson(responseBody)) {
                JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
                System.out.println("解析JSON成功");
            }
        } catch (Exception e) {
            System.err.println("使用HttpRequest接口调用失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}