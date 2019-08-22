package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.UserMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<User> findAll() {
        return userMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<User> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<User> users = (Page<User>) userMapper.selectAll();
        return new PageResult<User>(users.getTotal(), users.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<User> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return userMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<User> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<User> users = (Page<User>) userMapper.selectByExample(example);
        return new PageResult<User>(users.getTotal(), users.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param username
     * @return
     */
    public User findById(String username) {
        return userMapper.selectByPrimaryKey(username);
    }

    /**
     * 新增
     *
     * @param user
     */
    public void add(User user) {
        userMapper.insert(user);
    }

    /**
     * 修改
     *
     * @param user
     */
    public void update(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 删除
     *
     * @param username
     */
    public void delete(String username) {
        userMapper.deleteByPrimaryKey(username);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 用户名
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 密码，加密存储
            if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                criteria.andLike("password", "%" + searchMap.get("password") + "%");
            }
            // 注册手机号
            if (searchMap.get("phone") != null && !"".equals(searchMap.get("phone"))) {
                criteria.andLike("phone", "%" + searchMap.get("phone") + "%");
            }
            // 注册邮箱
            if (searchMap.get("email") != null && !"".equals(searchMap.get("email"))) {
                criteria.andLike("email", "%" + searchMap.get("email") + "%");
            }
            // 会员来源：1:PC，2：H5，3：Android，4：IOS
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andLike("sourceType", "%" + searchMap.get("sourceType") + "%");
            }
            // 昵称
            if (searchMap.get("nickName") != null && !"".equals(searchMap.get("nickName"))) {
                criteria.andLike("nickName", "%" + searchMap.get("nickName") + "%");
            }
            // 真实姓名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 使用状态（1正常 0非正常）
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }
            // 头像地址
            if (searchMap.get("headPic") != null && !"".equals(searchMap.get("headPic"))) {
                criteria.andLike("headPic", "%" + searchMap.get("headPic") + "%");
            }
            // QQ号码
            if (searchMap.get("qq") != null && !"".equals(searchMap.get("qq"))) {
                criteria.andLike("qq", "%" + searchMap.get("qq") + "%");
            }
            // 手机是否验证 （0否  1是）
            if (searchMap.get("isMobileCheck") != null && !"".equals(searchMap.get("isMobileCheck"))) {
                criteria.andLike("isMobileCheck", "%" + searchMap.get("isMobileCheck") + "%");
            }
            // 邮箱是否检测（0否  1是）
            if (searchMap.get("isEmailCheck") != null && !"".equals(searchMap.get("isEmailCheck"))) {
                criteria.andLike("isEmailCheck", "%" + searchMap.get("isEmailCheck") + "%");
            }
            // 性别，1男，0女
            if (searchMap.get("sex") != null && !"".equals(searchMap.get("sex"))) {
                criteria.andLike("sex", "%" + searchMap.get("sex") + "%");
            }

            // 会员等级
            if (searchMap.get("userLevel") != null) {
                criteria.andEqualTo("userLevel", searchMap.get("userLevel"));
            }
            // 积分
            if (searchMap.get("points") != null) {
                criteria.andEqualTo("points", searchMap.get("points"));
            }
            // 经验值
            if (searchMap.get("experienceValue") != null) {
                criteria.andEqualTo("experienceValue", searchMap.get("experienceValue"));
            }

        }
        return example;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送手机验证码
     *
     * @param phone 注册用户填写的手机号码（短信接收者的手机号码）
     */
    @Override
    public void sendMsg(String phone) {
        // 生成验证码：6位随机数字
        int min = 100000;
        int max = 999999;
        Random random = new Random();
        int code = random.nextInt(max);
        if (code < min) {//保证验证码是6位数
            code += min;
        }

        // 存一份到redis
        String key = "code_" + phone;
        redisTemplate.boundValueOps(key).set(String.valueOf(code));
        redisTemplate.boundValueOps(key).expire(5, TimeUnit.MINUTES);//有效时间5分钟

        // 发送一份消息给RabbitMQ
        /*
        此处的“消息”就包含两个内容：用户手机号 + 验证码

        RabbitTemplate类，public void convertAndSend(String exchange, String routingKey, final Object object)
            RedisTemplate类中也有convertAndSend()方法，注意不要写错了。
         */
        //
        Map<String, String> message = new HashMap<>();
        message.put("phone", phone);
        message.put("code", String.valueOf(code));
        //rabbitTemplate.convertAndSend("", "queue.sms", message);
        rabbitTemplate.convertAndSend("", "queue.sms", JSON.toJSONString(message));
    }

    /**
     * 添加新用户
     *
     * @param user    注册用户，存放前端页面上填写的用户信息
     * @param smsCode 验证码，用户在页面上输入的验证码。
     */
    @Override
    public void addUser(User user, String smsCode) {
        // 1 注册信息校对
        /*// 1.1 用户名校对    默认设置，用户名默认使用手机号
          // 前端页面都没有提供输入“用户名”的地方 ，哪里来的用户名非空判断
        if (null == user.getUsername() || "".equals(user.getUsername())) {
            user.setUsername(user.getPhone());
        }*/
        // 1.1 手机号校对：是否已经被注册
        User newUser = new User();
        newUser.setUsername(user.getPhone());// 默认使用手机号作为用户名。   页面不提供“用户名”的输入框
        int selectCount = userMapper.selectCount(newUser);// 统计用户名是user.getPhone()的记录条数
        if (selectCount > 0) {//有记录，手机号已注册过
            throw new RuntimeException("该手机号已注册！");
        }
        // 1.2 验证码校对
        String key = "code_" + user.getPhone();
        String codeInRedis = (String) redisTemplate.boundValueOps(key).get();
        if (codeInRedis == null) {
            throw new RuntimeException("未发送验证码或验证码已过期！");
        }
        if (!smsCode.equals(codeInRedis)) {
            throw new RuntimeException("验证码不正确！");
        }
        // 1.3 密码校对
        /*
        再次输入的密码是否一致，由前端页面自己去校对。只有两次输入的密码相同，点击“注册”才会发送注册信息给后台。
         */

        // 2 添加用户（to数据库）
        // 2.1 设置新用户信息  newUser     根据tb_user表来设置
        // username     主键
        user.setUsername(user.getPhone());
        // phone
        // password     非空      要求加密存储
        user.setPassword(user.getPassword());
        // created  非空
        user.setCreated(new Date());
        // updated  非空
        user.setUpdated(new Date());
        // status       账号状态     0非正常  1正常
        // 账号状态，应该有很多种，如：正常，禁用，删除，等
        user.setStatus("1");
        // is_mobile_check  0未验证    1已验证
        user.setIsMobileCheck("1");
        // is_email_check   0未验证    1已验证
        user.setIsEmailCheck("0");
        // points   积分  初始积分0
        user.setPoints(0);
        // experience_value 经验值 初始值0
        user.setExperienceValue(0);

        // 2.1 添加用户
        userMapper.insert(user);
    }

}
