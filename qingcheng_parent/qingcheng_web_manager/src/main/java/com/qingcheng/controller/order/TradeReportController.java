package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.order.TradeReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Feiyue
 * Date: 2019/8/27 23:07
 * Desc:
 */
@RestController
@RequestMapping("/tradeReport")
public class TradeReportController {

    @Reference // dubbo注解
    private TradeReportService tradeReportService;

    @GetMapping("/countPreDay")
    public Result countPreDay() {
        tradeReportService.countPreDay();
        return new Result();
    }
}
