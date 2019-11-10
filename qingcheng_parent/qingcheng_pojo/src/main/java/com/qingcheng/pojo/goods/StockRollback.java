package com.qingcheng.pojo.goods;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Author: Feiyue
 * Date: 2019/9/1 16:37
 * Desc:
 */
@Table(name = "tb_stock_rollback") //JPA注解
@Data
public class StockRollback {
    @Id
    private String orderId; //订单编号

    @Id
    private String skuId; //商品编号

    private Integer num; //回滚数量

    private String status; //回滚状态，0：未回滚；1：已回滚

    private Date createTime; //创建时间，回滚记录的创建时间

    private Date rollbackTime; //回滚时间
}
