package com.qingcheng.pojo.order;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/8/13 11:14
 * Desc: 对应qingcheng_order库中tb_category_report表
 */
@Table(name = "tb_category_report")
@Data
public class CategoryReport implements Serializable {

    /**
     * 1级商品分类id
     */
    @Id
    private Integer categoryId1;

    /**
     * 2级商品分类id
     */
    @Id
    private Integer categoryId2;

    /**
     * 3级商品分类id
     */
    @Id
    private Integer categoryId3;

    /**
     * 统计日期，年-月-日
     */
    @Id
    private Date countDate;

    /**
     * 销售数量
     */
    private Integer num;

    /**
     * 销售金额
     */
    private Integer money;

}
