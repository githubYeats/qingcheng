package com.qingcheng.pojo.order;

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

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public Integer getUv() {
        return uv;
    }

    public void setUv(Integer uv) {
        this.uv = uv;
    }

    public Integer getOrderdPersons() {
        return orderdPersons;
    }

    public void setOrderdPersons(Integer orderdPersons) {
        this.orderdPersons = orderdPersons;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public Integer getSkus() {
        return skus;
    }

    public void setSkus(Integer skus) {
        this.skus = skus;
    }

    public Integer getOrdersValid() {
        return ordersValid;
    }

    public void setOrdersValid(Integer ordersValid) {
        this.ordersValid = ordersValid;
    }

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Integer getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(Integer returnMoney) {
        this.returnMoney = returnMoney;
    }

    public Integer getPayPersons() {
        return payPersons;
    }

    public void setPayPersons(Integer payPersons) {
        this.payPersons = payPersons;
    }

    public Integer getPayOrders() {
        return payOrders;
    }

    public void setPayOrders(Integer payOrders) {
        this.payOrders = payOrders;
    }

    public Integer getPaySkus() {
        return paySkus;
    }

    public void setPaySkus(Integer paySkus) {
        this.paySkus = paySkus;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getPct() {
        return pct;
    }

    public void setPct(Integer pct) {
        this.pct = pct;
    }

    @Override
    public String toString() {
        return "TradeReport{" +
                "countDate=" + countDate +
                ", uv=" + uv +
                ", orderdPersons=" + orderdPersons +
                ", orders=" + orders +
                ", skus=" + skus +
                ", ordersValid=" + ordersValid +
                ", orderMoney=" + orderMoney +
                ", returnMoney=" + returnMoney +
                ", payPersons=" + payPersons +
                ", payOrders=" + payOrders +
                ", paySkus=" + paySkus +
                ", payMoney=" + payMoney +
                ", pct=" + pct +
                '}';
    }
}
