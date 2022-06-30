package com.wework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@MapperScan("com.wework.mapper")
public class WeWorkServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(WeWorkServiceApplication.class, args);
    }

}
