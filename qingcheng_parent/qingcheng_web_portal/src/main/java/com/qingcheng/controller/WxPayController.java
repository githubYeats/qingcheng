package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WxPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/31 11:13
 * Desc: 微信支付处理器P
 */
@RestController
@RequestMapping("/wxPay")
public class WxPayController {

    @Reference //dubbo注解
    private WxPayService wxPayService;

    @Reference //dubbo注解
    private OrderService orderService;

    @GetMapping("/createNative")
    public Map createNative(String orderId) throws Exception {
        // 获取当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 创建二维码
        String notifyUrl = "http://fengzheng.easy.echosite.cn/wxPay/notify.do";//需要定义一个回调处理器
        // 获取订单号
        Order order = orderService.findById(orderId);
        if (order != null) {
            if ("0".equals(order.getPayStatus()) && "0".equals(order.getOrderStatus()) && username.equals(order.getUsername())) {
                // 生成二维码 ，并调用notifyUrl
                return wxPayService.createNative(orderId, order.getPayMoney(), notifyUrl);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 回调
     */
    @RequestMapping("/notify")
    public void notifyLogic(HttpServletRequest request) throws IOException {
        System.out.println("支付成功回调。。。。");
    }
}
