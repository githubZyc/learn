package com.wework.service;

import com.alibaba.fastjson.JSONObject;
import com.wework.config.QywxInnerConfig;
import com.wework.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeWorkService {
    private final static Logger logger = LoggerFactory.getLogger("test");

    @Autowired
    QywxInnerConfig qywxInnerConfig;

    /**
     **
     * 发送消息
     * @author ZYC
     * @date   2022/6/29 14:53 []
     * @return java.lang.String
     */
    //消息推送
    //发送消息
    public Map sendMsg() {

        //https://open.work.weixin.qq.com/api/doc/90001/90143/93414
        String accessToken = this.getAccessToken(qywxInnerConfig.getCorpId());

        String url = String.format(qywxInnerConfig.getMessageSendUrl(),accessToken) ;
        //获取企业的agentid
        String agentId = qywxInnerConfig.getAgentId();

        JSONObject postJson = new JSONObject();
        postJson.put("msgtype","text");
        postJson.put("agentid",agentId);

        JSONObject testJson =  new JSONObject();
        testJson.put("content","https://xgs.newgscloud.com/pages/2022/05/07/322717484fdb435aab814e8645f1ec63.html");
        postJson.put("text",testJson);

        postJson.put("touser","@all");
        JSONObject response = RestUtils.post(url,postJson);
        //获取错误日志
        if(response.containsKey("errcode") && (Integer) response.get("errcode") != 0){
            logger.error(response.toString());
        }
        return  response;

    }

    public String getAccessToken(String corpId){
        String result = "";
        String agentSecret = qywxInnerConfig.getAgentSecret();

        String  accessTokenUrl =  String.format(qywxInnerConfig.getAccessTokenUrl(),corpId,agentSecret);
        Map response = RestUtils.get(accessTokenUrl );
        //获取错误日志
        if(response.containsKey("errcode") && (Integer) response.get("errcode") != 0){
            logger.error(response.toString());
        }else{
            result = (String) response.get("access_token");
        }

        return result;

    }

}
