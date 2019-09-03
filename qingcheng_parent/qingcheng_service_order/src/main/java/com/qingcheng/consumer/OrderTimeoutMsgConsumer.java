package com.qingcheng.consumer;

import com.qingcheng.service.order.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Author: Feiyue
 * Date: 2019/9/3 12:20
 * Desc: 超时未支付订单消息的消费者
 */
public class OrderTimeoutMsgConsumer implements MessageListener {

    @Autowired
    private OrderService orderService;

    @Override
    public void onMessage(Message message) {
        // 取消息
        /*
        消息内容：一个一个订单号

        RabbitMQ设计，是让消息消费者逐条地取消息，消费完，即处理完业务后，再去取一条消息。
         */
        String orderId = new String(message.getBody());

        // 业务处理
        try {
            orderService.orderTimeoutLogic(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，则记录日志
        }
    }
}
