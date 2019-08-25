package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/24 23:40
 * Desc:
 */

@RestController
@RequestMapping("/login")
public class LoginController {

    @GetMapping("/username")
    public Map<String, String> username() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // 该username()在sring-security.xml中配置了"匿名访问权限"
        // 这样一来，当未登录时去访问网站，Spring Security默认返回一个用户名"anonymousUser"
        if("anonymousUser".equals(name)){
            name="";
        }

        Map<String, String> map = new HashMap<>();
        map.put("username", name);
        return map;

    }
}