package com.qingcheng.service.order;

import com.qingcheng.pojo.order.TradeReport;

import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/27 18:58
 * Desc:
 */
public interface TradeReportService {

    /**
     * 定时任务，在凌晨1点执行统计，将统计结果插入到交易统计表
     */
    public void countPreDay();

    /**
     * 查询全部
     * @return
     */
    public List<TradeReport> findAll();

    /**
     * 按日期段查询交易统计数据
     *
     * @param dateBegin 开始日期
     * @param dateEnd   结束日期
     * @return
     */
    public List<Map> findByDateRange(String dateBegin, String dateEnd);

}
