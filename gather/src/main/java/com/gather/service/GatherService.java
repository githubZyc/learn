package com.gather.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/19 16:04
 */
@Service
public class GatherService {
    String videoUrl = "https://www.btnull.org/";

    /**
     * @description
     * @author  zhengyanchuang
     * @date    2023/5/19 16:04
     * @param
     * @return  java.lang.String
     */
    public String allVideo() {
        Document parse = (Document) Jsoup.connect(videoUrl);
        Element body = parse.body();
        Elements select = body.select(".c0");
        return "success";
    }
}
