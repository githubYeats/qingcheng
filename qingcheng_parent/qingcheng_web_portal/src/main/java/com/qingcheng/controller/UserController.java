package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 添加新用户
     *
     * @param user
     * @param smsCode
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody User user, String smsCode) {
        // 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = user.getPassword();//用户输入的明文密码
        String newPwd = passwordEncoder.encode(password);
        user.setPassword(newPwd);
        // 调用service层，添加用户
        userService.addUser(user, smsCode);
        return new Result();
    }

}
