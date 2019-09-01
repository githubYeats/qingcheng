package com.qingcheng.consumer;

import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.StockRollbackService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/9/1 17:03
 * Desc: 库存回滚消息消费者
 */
public class StockRollBackMsgConsumer implements MessageListener {

    @Autowired
    private StockRollbackService stockRollbackService;

    @Override
    public void onMessage(Message message) {
        try {
            // 提取消息
            String msg = new String(message.getBody());
            List<OrderItem> orderItemList = JSON.parseArray(msg, OrderItem.class);

            // 回滚库存
            stockRollbackService.addList(orderItemList);
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，则记录日志
        }

    }
}
