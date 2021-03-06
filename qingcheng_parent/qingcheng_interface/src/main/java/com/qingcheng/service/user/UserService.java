package com.qingcheng.service.user;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.user.User;

import java.util.*;

/**
 * user业务逻辑层
 */
public interface UserService {


    public List<User> findAll();


    public PageResult<User> findPage(int page, int size);


    public List<User> findList(Map<String,Object> searchMap);


    public PageResult<User> findPage(Map<String,Object> searchMap,int page, int size);


    public User findById(String username);

    public void add(User user);


    public void update(User user);


    public void delete(String username);

    /**
     * 发送短信验证码
     * @param phone 注册用户填写的手机号码（短信接收者的手机号码）
     */
    public void sendMsg(String phone);

    /**
     * 添加用户
     * @param user  注册用户，存放前端页面上填写的用户信息
     * @param smsCode   验证码，用户在页面上输入的验证码。
     */
    public void addUser(User user, String smsCode);

}
