package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/13 11:20
 * Desc:
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {


    /**
     * 通过指定日期，查询商品类目销售统计数据
     * @param date
     * @return
     */
    @Select("SELECT category_id1 categoryId1, category_id2 categoryId2, category_id3 categoryId3, Date_format ( t1.pay_time, '%Y-%m-%d' ) countDate, sum( t2.num ) num,sum( t2.pay_money ) money " +
            "FROM tb_order t1, tb_order_item t2 " +
            "WHERE t1.id = t2.order_id  AND t1.pay_status = '1'  AND t1.is_delete = '0'  AND Date_format ( t1.pay_time, '%Y-%m-%d' ) = #{date} " +
            "GROUP BY category_id1, category_id2, category_id3, Date_format ( t1.pay_time, '%Y-%m-%d' );")
    public List<CategoryReport> categoryReport(@Param("date") LocalDate date);

    /**
     * 1级分类商品指定时间段的销售统计
     * @param dateBegin 查询开始日期      左闭
     * @param dateEnd   查询结束日期      右闭
     * @return
     */
    /*@Select("select category_id1 categoryId1, sum(num) num , sum(money) money " +
            "from tb_category_report " +
            "where count_date between #{dateBegin} and #{dateEnd} " +
            "group by category_id1;")*/
    @Select("select t1.category_id1 categoryId1, t2.name, sum(t1.num) num , sum(t1.money) money " +
            "from tb_category_report t1, v_tb_category1 t2 " +
            "where t1.category_id1=t2.id and t1.count_date between #{dateBegin} and #{dateBegin} " +
            "group by t1.category_id1, t2.name;")
    public List<Map> category1Count(@Param("dateBegin") String dateBegin, @Param("dateEnd")String dateEnd);
}
