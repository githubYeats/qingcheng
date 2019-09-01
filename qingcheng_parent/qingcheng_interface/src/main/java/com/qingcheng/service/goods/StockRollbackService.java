package com.qingcheng.service.goods;

import com.qingcheng.pojo.order.OrderItem;

import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/9/1 16:47
 * Desc: 商品库存回滚接口   sku
 */
public interface StockRollbackService {

    /**
     * 添加回滚记录
     *
     * @param orderItemList
     */
    public void addList(List<OrderItem> orderItemList);

    /**
     * 执行库存回滚
     */
    public void stockRollback();

}
