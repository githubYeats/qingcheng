package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/6 19:57
 * Desc:
 */


/*
org.springframework.security.core.userdetails.UserDetailsService;
Spring Security提供的一个接口：用户从数据库中提取用户信息，来进行登录认证！
 */
public class UserDetailServiceImpl implements UserDetailsService {

    /**
     * com.qingcheng.service.system.AdminService
     * 对应着qingcheng_system库，tb_admin表
     */
    @Reference //dubbo注解
    private AdminService adminService;

    /**
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //System.out.println("经过了UserDetailServiceImpl");

        //qingcheng_system库，tb_admin表
        Map<String, Object> map=new HashMap<>();
        map.put("loginName",username);
        map.put("status","1");//账号为开启状态

        // 条件查询，代码生成器自动生成的方法
        // public List<Admin> findList(Map<String, Object> searchMap)
        List<Admin> list = adminService.findList(map);
        if(list.size()==0){ // 没查到用户，方法结束
            return null;
        }

        //实际项目中应该从数据库中提取用户的角色列表
        List<GrantedAuthority> grantedAuthorities=new ArrayList<GrantedAuthority>();
        grantedAuthorities.add( new SimpleGrantedAuthority("ROLE_ADMIN"));

        //import org.springframework.security.core.userdetails.User;
        /*
        public User(String username, String password, Collection<? extends GrantedAuthority> authorities)
         */
        User user = new User(username, list.get(0).getPassword(), grantedAuthorities);

        return user;
    }
}
