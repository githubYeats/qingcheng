package com.qingcheng.consumer;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/21 23:39
 * Desc: Listener(interface) to receive asynchronous delivery of Amqp Messages.
 */
/*
RabbitMQ 是一个由 Erlang 语言开发的 AMQP 的开源实现。
AMQP ：Advanced Message Queue，高级消息队列协议。
    它是应用层协议的一个开放标准，为面向消息的中间件设计，基于此协议的客户端与消息中间件可传递消息，
    并且不受产品、开发语言等条件的限制。
    消息发送与接收是异步方式。
 */
public class SmsMessageListener implements MessageListener {


    @Autowired
    private SmsUtil smsUtil;

   /* @Value("${sms.signName}")
    private String signName;*/

    @Value("${sms.templateCode}")
    private String templateCode;

    @Value("${sms.templateParam}")
    private String templateParam;

    /**
     * 接收异步消息，打印到控制台
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        // 接收消息（网络上传输以字节数据进行）
        byte[] messageBody = message.getBody();
        String messageString = new String(messageBody);
        Map<String, String> messageMap = JSON.parseObject(messageString, Map.class);

        // 提取消息
        String phone = messageMap.get("phone");
        String code = messageMap.get("code");
        System.out.println("手机号:" + phone + ", 验证码:" + code);

        // 发送短消息
        // SmsUtil工具类   sendSms(String phoneNumbers, String signName, String templateCode, String templateParam)
        // sms.param={"code":"[value]"}
        smsUtil.sendSms(phone, templateCode, templateParam.replace("[value]", code));

        /*// 接收消息（网络上传输以字节数据进行）
        byte[] messageBody = message.getBody();
        System.out.println(Arrays.asList(messageBody));

        // byte --> String
        String messageString = new String(messageBody);//json字符串，如：{"code":"165093","phone":"1234567890"}
        System.out.println(messageString);

        // String-->Map对象       方便提取单个属性
        Map<String, String> messageMap = JSON.parseObject(messageString, Map.class);

        // 提取消息
        String phone = messageMap.get("phone");
        String code = messageMap.get("code");
        System.out.println("手机号:" + phone + ", 验证码:" + code);*/
    }
}
