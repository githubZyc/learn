package com.wework.controller;

import com.wework.service.GroupChatService;
import com.wework.service.WeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class ContentController {
    @Autowired
    WeWorkService weWorkService;
    @Autowired
    GroupChatService groupChatService;

    @RequestMapping("/index")
    public String toHelloPage(){
        return "hello";
    }

    @RequestMapping("/content")
    public String toContentPage(){
        return "content";
    }

    @RequestMapping("/wz")
    public String toWZPage(){
        return "wz";
    }

    @RequestMapping("/sendMsg")
    @ResponseBody
    public String sendMsg(){
        groupChatService.addMsgTemplate();
        return "ok";
    }

    @RequestMapping("/wzBack")
    public String toWZBack(){
        return "wzBack";
    }
}
