package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/13 12:04
 * Desc:
 */
@Service(interfaceClass = CategoryReportService.class) // dubbo注解，将该类注册为服务
public class CategoryReportServiceImpl implements CategoryReportService {

    @Autowired // Spring注解
    private CategoryReportMapper categoryReportMapper;

    /**
     * 通过指定日期，查询商品类目销售统计数据
     *
     * @param date
     * @return
     */
    @Override
    public List<CategoryReport> categoryReport(LocalDate date) {
        return categoryReportMapper.categoryReport(date);
    }

    /**
     * 创建数据，即每日凌晨1:00时，统计前一日的商品销售统计，并将统计结果添加到数据库。
     */
    @Override
    @Transactional //Spring注解，事务管理
    public void createData() {
        // 1.销售统计
        //LocalDate date = LocalDate.now().minusDays(1);
        LocalDate date = LocalDate.of(2019,8,12);
        List<CategoryReport> categoryReportList = categoryReportMapper.categoryReport(date);

        for (CategoryReport categoryReport : categoryReportList) {
            // 2.更新数据库
            categoryReportMapper.insert(categoryReport);
        }
    }

    /**
     * 1级分类商品指定时间段的销售统计
     * @param dateBegin 查询开始日期      左闭
     * @param dateEnd   查询结束日期      右闭
     * @return
     */
    @Override
    public List<Map> category1Count(String dateBegin, String dateEnd) {
        return categoryReportMapper.category1Count(dateBegin, dateEnd);
    }
}
