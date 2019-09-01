package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/9/1 16:37
 * Desc:
 */
@Table(name = "tb_stock_rollback") //JPA注解
public class StockRollback {
    @Id
    private String orderId; //订单编号
    @Id
    private String skuId; //商品编号
    private Integer num; //回滚数量
    private String status; //回滚状态，0：未回滚；1：已回滚
    private Date createTime; //创建时间，回滚记录的创建时间
    private Date rollbackTime; //回滚时间

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getRollbackTime() {
        return rollbackTime;
    }

    public void setRollbackTime(Date rollbackTime) {
        this.rollbackTime = rollbackTime;
    }

    @Override
    public String toString() {
        return "StockRollback{" +
                "orderId='" + orderId + '\'' +
                ", skuId='" + skuId + '\'' +
                ", num=" + num +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", rollbackTime=" + rollbackTime +
                '}';
    }
}
