package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/8/10 14:15
 * Desc: AuthenticationSuccessHandler 认证成功处理器。
 *       Spring Security框架提供的登录成功处理器，登录成功后框架会自动调用内部方法。
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService  loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //System.out.println("AuthenticationSuccessHandlerImpl is running......");
        //获取当前登录人名称
        //String loginName = SecurityContextHolder.getContext().getAuthentication().getName();

        /*
        --------------------记录用户登录日志---------------------
        即要在tb_login_log表中插入一个记录
            id 数据库自动分配
            login_name
            ip
            browser_name        利用外部工具类：WebUtil
            location            利用外部工具类：WebUtil     根据ip去查询城市
            login_time
         */
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginName(authentication.getName());
        loginLog.setIp(request.getRemoteAddr());
        loginLog.setLoginTime(new Date());
        loginLog.setLocation(WebUtil.getCityByIP(request.getRemoteAddr()));
        String agent = request.getHeader("user-agent");
        loginLog.setBrowserName(WebUtil.getBrowserName(agent));
        //更新数据库
        loginLogService.add(loginLog);
        //跳转页面
        request.getRequestDispatcher("/main.html").forward(request, response);
    }
}
