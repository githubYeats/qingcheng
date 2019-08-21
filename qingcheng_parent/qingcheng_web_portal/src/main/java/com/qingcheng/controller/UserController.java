package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Feiyue
 * Date: 2019/8/21 22:55
 * Desc:
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference //dubbo注解，从注册中心获取服务
    private UserService userService;

    /**
     * 发送消息
     *
     * @param phone
     * @return
     */
    //@GetMapping(value = "/sendMsg")
    @GetMapping("/sendMsg")
    public Result sendMsg(String phone) {
        userService.sendMsg(phone);
        return new Result();
    }

}
