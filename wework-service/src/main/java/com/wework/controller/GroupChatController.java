package com.wework.controller;

import com.wework.service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groupChat")
public class GroupChatController {
    @Autowired
    GroupChatService groupChatService;

    /**
     **
     * 创建群聊
     * @author ZYC
     * @date   2022/6/29 15:29 []
     * @return java.lang.String
     */
    @RequestMapping("/create")
    public String create(){
        return groupChatService.create();
    }
}
