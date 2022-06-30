package com.wework.service;

import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import com.wework.config.QywxInnerConfig;
import com.wework.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class GroupChatService {
    private final static Logger logger = LoggerFactory.getLogger("test");

    @Autowired
    QywxInnerConfig qywxInnerConfig;
    @Autowired
    WeWorkService weWorkService;

    //创建企业群发
    public String addMsgTemplate(){

        /**
         * {
         *     "chat_type": "single",
         *     "external_userid": [
         *         "woAJ2GCAAAXtWyujaWJHDDGi0mACAAAA",
         *         "wmqfasd1e1927831123109rBAAAA"
         *     ],
         *     "sender": "zhangsan",
         *     "text": {
         *         "content": "文本消息内容"
         *     },
         *     "attachments": [
         *           {
         *         "msgtype": "link",
         *         "link": {
         *             "title": "消息标题",
         *             "picurl": "https://example.pic.com/path",
         *             "desc": "消息描述",
         *             "url": "https://example.link.com/path"
         *          }
         *           },
         *     】
         * }
         */
        String accessToken = weWorkService.getAccessToken(qywxInnerConfig.getCorpId());
        String url = String.format(qywxInnerConfig.getExtcontactAddMsgTemplateUrl(),accessToken) ;

        JSONObject postJson = new JSONObject();
        postJson.put("chat_type","group");

        postJson.put("sender","ZhengYanChuang");

        List<JSONObject> attachments = new ArrayList();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype","link");
        JSONObject linkJsonObject = new JSONObject();
        linkJsonObject.put("title","冰雪运动“热”高原");
        linkJsonObject.put("picurl","https://xgs.newgscloud.com/upload/news/image/2022/02/17/1645090400466036521.jpg?h=3862&w=5792");
        linkJsonObject.put("desc","冰雪运动“热”高原冰雪运动“热”高原");
        linkJsonObject.put("url","https://xgs.newgscloud.com/pages/2022/02/17/79b08daf682145c7a01d630fa9774833.html");
        jsonObject.put("link",linkJsonObject);
        attachments.add(jsonObject);
        postJson.put("attachments",attachments);


        JSONObject response = RestUtils.post(url,postJson);
        //获取错误日志
        if(response.containsKey("errcode") && (Integer) response.get("errcode") != 0){
            logger.error(response.toString());
        }
        return response.toString();

    }

    public String create() {
        String createGroupChatUrl = QywxInnerConfig.createGroupChat;
        String accessToken = weWorkService.getAccessToken(qywxInnerConfig.getCorpId());
        String url = String.format(createGroupChatUrl,accessToken) ;
        /**
         * {
         *     "name" : "NAME",
         *     "owner" : "userid1",
         *     "userlist" : ["userid1", "userid2", "userid3"],
         *     "chatid" : "CHATID"
         * }
         */
        JSONObject postJson = new JSONObject();
        postJson.put("name","美好政务自建应用群聊");
        postJson.put("userlist", Arrays.asList(new String[]{"ZhengYanChuang","hetingting"}));
        JSONObject response = RestUtils.post(url,postJson);
        //获取错误日志
        if(response.containsKey("errcode") && (Integer) response.get("errcode") != 0){
            logger.error(response.toString());
        }
        return response.toJSONString();
    }
}
