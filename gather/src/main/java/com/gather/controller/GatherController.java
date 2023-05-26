package com.gather.controller;

import com.gather.service.GatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/19 15:43
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/g")
public class GatherController {
    private final GatherService gatherService;
    /**
     * @description  获取到所有视频下载链接
     * @author  zhengyanchuang
     * @date    2023/5/19 16:03
     * @param
     * @return  java.lang.String
     */
    @RequestMapping("/all")
    public String allVideo(){
        return gatherService.allVideo();
    }
}
