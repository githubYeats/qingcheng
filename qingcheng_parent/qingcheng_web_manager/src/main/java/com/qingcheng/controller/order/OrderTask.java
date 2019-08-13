package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.OrderConfig;
import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/8/8 21:33
 * Desc: 订单超时处理
 */
@Component
public class OrderTask {
    @Reference //dubbo注解，从注册中心获取服务
    private OrderConfig orderConfig;

    /**
     * 设置规则：每2分钟处理一次请求，将60分钟前未付款订单关闭
     */
    //@Scheduled(cron = "0 0/2 * * * ?")//每隔2分钟，在0秒时，执行操作
   // @Scheduled(cron = "* * * * * ?")//每秒执行一次
   /* public void orderTimeoutLogic(){
        System.out.println(new Date());
        //orderConfig.getOrderTimeout(); // 先不执行订单关闭功能
    }*/

    @Reference
    private CategoryReportService categoryReportService;

    /**
     * 定时任务，商品类目销售统计
     */
    //@Scheduled(cron = "0 0 1 * * ?") // 每日01:00:00，执行该任务
   /* @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次。  秒的位置设置0，代表每个0秒时执行
    public void createCategoryReportData(){
        System.out.println("生成了商品类目统计数据，已添加到数据库！");
        categoryReportService.createData();
    }*/

}
