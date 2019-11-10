package com.qingcheng.pojo.order;

import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/8/7 18:36
 * Desc: 组合实体类  tb_order表 + tb_order_item表      qingcheng_order库
 */
@Data
public class OrderItemOrder implements Serializable {
    private Order order;

    private List<OrderItem> orderItemList;
}
