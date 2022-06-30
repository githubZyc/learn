package com.wework.controller;

import com.wework.service.WeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeWorkController {
    @Autowired
    WeWorkService weWorkService;

    @RequestMapping("/create/center")
    public String toHelloPage(){
        return "hello";
    }
}
