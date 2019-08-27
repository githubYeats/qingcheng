package com.qingcheng.service.order;

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
}
