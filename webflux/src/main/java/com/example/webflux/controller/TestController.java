package com.example.webflux.controller;

import com.example.webflux.entity.User;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/6/15 20:00
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello,WebFlux!";
    }

    @GetMapping("/user")
    public Mono<User> getUser(){
        User user = new User();
        user.setName("LCZ");
        user.setDesc("LCZ的Java自习室！！！");
        return Mono.just(user);
    }

    @GetMapping("/users")
    public Flux<User> getUsers(){
        User user = new User();
        user.setName("LCZ");
        user.setDesc("LCZ的Java自习室！！！");

        User user2 = new User();
        user2.setName("LCZ2");
        user2.setDesc("LCZ的Java自习室！！2！22");
        return Flux.just(user,user2);
    }
}
