package com.qingcheng.service.index;

import java.io.IOException;

/**
 * Author: Feiyue
 * Date: 2019/8/30 18:41
 * Desc: Elasticsearch索引服务
 */
public interface IndexService {

    /**
     * 处理索引消息
     *
     * @param body
     */
    public void indexMsgDeal(byte[] body) throws IOException;

    /**
     * 从RabbitMQ中取消息（spuId），将对应spu下所有的sku添加到elasticsearch中
     *
     * @param spuId
     */
    public void addData2ESBySpuId(String spuId) throws IOException;

    /**
     * 从RabbitMQ中取消息（spuId），将elasticsearch中对应spu下所有的sku删除掉
     *
     * @param spuId
     */
    public void deleteDataFormESBySpuId(String spuId) throws IOException;
}
