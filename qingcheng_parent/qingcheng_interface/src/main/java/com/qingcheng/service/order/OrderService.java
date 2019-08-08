package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItemOrder;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String,Object> searchMap);


    public PageResult<Order> findPage(Map<String,Object> searchMap,int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);


    public void delete(String id);

    /**
     * 通过订单id查询订单及订单项
     * @param id    订单id，分布式id
     * @return
     */
    public OrderItemOrder findOrderByOrderId(String id);

    /**
     * 批量发货
     * @param ids   订单编号数组
     * @return  成功发货的订单编号数组
     */
    public List<String> batchConsign(String[] ids);

    /**
     * 批量发货
     * @param orderList 经过筛选的"待发货"状态的订单集合
     */
    public void batchSend(List<Order> orderList);



}
