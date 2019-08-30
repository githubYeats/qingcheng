package com.qingcheng.consumer;

import com.qingcheng.service.page.PageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Author: Feiyue
 * Date: 2019/8/30 11:04
 * Desc: 商品详情页消息监听类
 */
public class PageMsgListener implements MessageListener {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        byte[] body = message.getBody();

        // 调用service进行逻辑处理
        if (body != null) {
            pageService.pageMsgDeal(message.getBody());
        }
    }
}
