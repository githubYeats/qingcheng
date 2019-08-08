package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.OrderConfig;
import com.qingcheng.service.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * Author: Feiyue
 * Date: 2019/8/8 21:33
 * Desc: 订单超时处理
 */
@Component
public class OrderTask {

    /*//示例程序
    @Scheduled(cron = "* * * * * ?")
    public void orderTimeoutLogic(){
        System.out.println("@");
    }*/

    @Reference //dubbo注解，从注册中心获取服务
    private OrderConfig orderConfig;


    /**
     * 设置规则：每2分钟处理一次请求，将60分钟前未付款订单关闭
     */
    @Scheduled(cron = "0 0/2 * * * ?")//每隔2分钟，在0秒时，执行操作
    public void orderTimeoutLogic(){
        System.out.println("");
        orderConfig.getOrderTimeout();
    }
}
