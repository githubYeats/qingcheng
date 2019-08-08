package com.qingcheng.pojo.order;

import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/8/7 18:36
 * Desc: 组合实体类  tb_order表 + tb_order_item表      qingcheng_order库
 */
public class OrderItemOrder implements Serializable {
    private Order order;

    private List<OrderItem> orderItemList;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
