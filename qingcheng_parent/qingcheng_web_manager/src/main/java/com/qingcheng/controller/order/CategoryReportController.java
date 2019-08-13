package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/13 12:10
 * Desc:
 */
@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference //dubbo注解
    private CategoryReportService categoryReportService;

    /**
     * 前日商品销售统计：以一级目录为准
     *
     * @return
     */
    @GetMapping("/countPre")
    public List<CategoryReport> countPre() {
        //LocalDate date = LocalDate.now().minusDays(1);// 获取前一天日期
        LocalDate date = LocalDate.of(2019, 8, 12);
        System.out.println(date);
        return categoryReportService.categoryReport(date);
    }

    /**
     * 统计1级分类商品指定时间段的销售情况
     * @param dateBegin 开始时间
     * @param dateEnd   结束时间
     * @return
     */
    @GetMapping("/category1Count")
    public List<Map> category1Count(String dateBegin, String dateEnd) {
        return categoryReportService.category1Count(dateBegin, dateEnd);
    }
}
