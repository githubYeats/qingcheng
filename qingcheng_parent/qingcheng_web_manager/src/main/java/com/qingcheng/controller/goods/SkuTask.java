package com.qingcheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.StockRollbackService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/9/1 17:22
 * Desc:
 */
@Component
public class SkuTask {

    @Reference //dubbo注解
    private StockRollbackService stockRollbackService;

    /**
     * 倒计时
     */
    @Scheduled(cron = "* * * * * ?")//每秒执行一次
    public void countDown() {
        System.out.println(new Date());
    }


     //@Scheduled(cron = "0 0 0/1 * * ?") //每小时执行一次
    @Scheduled(cron = "0 * * * * ?") //每分钟执行一次
    public void doStockRollback() {
        stockRollbackService.stockRollback();
    }
}
