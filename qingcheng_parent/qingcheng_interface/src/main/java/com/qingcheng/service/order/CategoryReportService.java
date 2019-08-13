package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/13 12:02
 * Desc:
 */
public interface CategoryReportService {


    /**
     * 通过指定日期，查询商品类目销售统计数据
     * @param date
     * @return
     */
    public List<CategoryReport> categoryReport(LocalDate date);

    /**
     * 创建数据，即每日凌晨1:00时，统计前一日的商品销售统计，并将统计结果添加到数据库。
     */
    public void createData();


    /**
     * 1级分类商品指定时间段的销售统计
     * @param dateBegin 查询开始日期      左闭
     * @param dateEnd   查询结束日期      右闭
     * @return
     */
    public List<Map> category1Count(String dateBegin, String dateEnd);

}
