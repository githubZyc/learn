package com.haigrid.media;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/19 15:42
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.haigrid"})
@MapperScan("com.haigrid.media.mapper")
public class ShiroTestApplication {
    public static void main(String[] args){

        SpringApplication.run(ShiroTestApplication.class, args);
    }

}
