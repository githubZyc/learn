package com.haigrid.media.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haigrid.media.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2023-05-24
 */
@RestController
@RequestMapping("/sys-user")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    /**
     * 登录
     */
    @GetMapping("/login")
    public String login(String username,String password) {
        try {
//            //1.构造登录令牌
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            //获取subject
            Subject subject = SecurityUtils.getSubject();
            //调用login方法完成认证
            subject.login(token);
            Object principal = subject.getPrincipal();
            //获取SessionId
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequiresPermissions(value = "sys:role:view")
    @GetMapping("/list")
    public String all() {
        return JSONObject.toJSONString(sysUserService.list(new LambdaQueryWrapper<>()));
    }
}

