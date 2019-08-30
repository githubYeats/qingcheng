package com.qingcheng.dao;

import com.qingcheng.pojo.order.TradeReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/27 18:41
 * Desc:
 */
public interface TradeReportMapper extends Mapper<TradeReport> {

    /**
     * 按日期段查询交易统计数据
     *
     * @param dateBegin 开始日期
     * @param dateEnd   结束日期
     * @return
     */
    @Select("select * from tb_trade_report where count_date between #{dateBegin} and #{dateEnd}")
    public List<Map> findByDateRange(@Param("dateBegin") String dateBegin, @Param("dateEnd") String dateEnd);

}
