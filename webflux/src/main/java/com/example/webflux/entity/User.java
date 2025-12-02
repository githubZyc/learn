package com.example.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhengyanchuang
 * @version 1.0
 * @description TODO
 * @date 2023/6/15 20:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private String desc;
}
