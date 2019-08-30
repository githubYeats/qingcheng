package com.qingcheng.service.page;

/**
 * Author: Feiyue
 * Date: 2019/8/30 15:09
 * Desc:
 */
public interface PageService {

    /**
     * 根据上架spu商品的spuId生成对应的sku详情页面
     *
     * @param spuId
     */
    public void createPagesBySpuId(String spuId);

    /**
     * 根据下架spu商品的spuId删除对应的sku详情页面
     *
     * @param spuId
     */
    public void deletePagesBySpuId(String spuId);

    /**
     * 消息处理
     * @param body 消息体
     */
    public void pageMsgDeal(byte[] body);

}
