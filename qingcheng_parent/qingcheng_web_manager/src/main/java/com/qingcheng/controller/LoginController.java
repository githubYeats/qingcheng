package com.qingcheng.controller;

import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/10 12:39
 * Desc:
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Reference
    private LoginLogService loginLogService;

    /**
     * 查询当前登录人名称
     *
     * @return
     */
    @GetMapping("/loginName")
    public Map<String, String> loginName() {
        // 获取当前登录用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        // 响应
        Map<String, String> map = new HashMap<>();
        map.put("loginName", loginName);
        return map;
    }

    @GetMapping("/findLoginLog")
    public PageResult<LoginLog> findLoginLog(int page, int size) {
        //查询条件
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        map.put("loginName", loginName);
        return loginLogService.findPage(map, page, size);
    }
}
