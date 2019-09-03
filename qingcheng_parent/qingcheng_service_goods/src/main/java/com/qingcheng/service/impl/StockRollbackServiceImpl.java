package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.StockRollbackMapper;
import com.qingcheng.pojo.goods.StockRollback;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockRollbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/9/1 16:46
 * Desc:
 */
@Service(interfaceClass = StockRollbackService.class)
public class StockRollbackServiceImpl implements StockRollbackService {

    @Autowired
    private StockRollbackMapper stockRollbackMapper;

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 添加回滚记录
     *
     * @param orderItemList
     */
    @Override
    public void addList(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            StockRollback stockRollback = new StockRollback();
            stockRollback.setSkuId(orderItem.getSkuId());
            stockRollback.setOrderId(orderItem.getOrderId());
            stockRollback.setNum(orderItem.getNum());
            stockRollback.setCreateTime(new Date());
            stockRollback.setStatus("0");
            stockRollbackMapper.insert(stockRollback);
        }
    }

    /**
     * 执行库存回滚
     * 针对下单异常导致的数据不一致
     */
    @Override
    @Transactional // 事务控制
    public void stockRollback() {
        // 查询库存回滚表中回滚状态为0的所有记录
        StockRollback stockRollback = new StockRollback();
        stockRollback.setStatus("0");
        List<StockRollback> stockRollbackList = stockRollbackMapper.select(stockRollback);

        // 执行库存回滚
        for (StockRollback rollback : stockRollbackList) {
            // 增加库存
            skuMapper.stockDeduct(rollback.getSkuId(), -rollback.getNum());//添加负数，即增加
            // 减少销量
            skuMapper.addSaleNum(rollback.getSkuId(), -rollback.getNum());
            // 修改回滚记录表
            rollback.setStatus("1");
            rollback.setRollbackTime(new Date());
            stockRollbackMapper.updateByPrimaryKeySelective(rollback);
        }
    }
}
