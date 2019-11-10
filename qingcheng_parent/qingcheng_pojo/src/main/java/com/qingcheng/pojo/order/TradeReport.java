package com.qingcheng.pojo.order;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/8/27 18:30
 * Desc: 交易统计表      对应tb_trade_report
 */
@Table(name = "tb_trade_report")
@Data
public class TradeReport implements Serializable {
    @Id
    private Date countDate;//统计日期

    private Integer uv; //浏览人数

    private Integer orderdPersons; //下单人数

    private Integer orders; //订单数

    private Integer skus; // 下单件数

    private Integer ordersValid; //有效订单数

    private Integer orderMoney; //下单金额

    private Integer returnMoney; //退款金额

    private Integer payPersons; //付款人数

    private Integer payOrders; //付款订单数

    private Integer paySkus; //付款件数

    private Integer payMoney; //付款金额
    
    private Integer pct; //客单价
}
