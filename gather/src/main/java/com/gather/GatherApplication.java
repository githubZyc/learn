package com.gather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/5/19 15:42
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatherApplication {
    public static void main(String[] args){

        SpringApplication.run(GatherApplication.class, args);
    }

}
