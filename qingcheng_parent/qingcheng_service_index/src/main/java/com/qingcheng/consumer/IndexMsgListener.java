package com.qingcheng.consumer;

import com.qingcheng.service.index.IndexService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * Author: Feiyue
 * Date: 2019/8/30 11:04
 * Desc: 索引数据的监听类
 */
public class IndexMsgListener implements MessageListener {

    @Autowired
    private IndexService indexService;

    @Override
    public void onMessage(Message message) {
        // 接收消息
        byte[] body = message.getBody();

        // 调用service处理业务逻辑
        try {
            indexService.indexMsgDeal(body);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
