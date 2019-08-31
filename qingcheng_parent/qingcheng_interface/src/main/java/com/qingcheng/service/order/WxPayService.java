package com.qingcheng.service.order;

import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/31 10:58
 * Desc: 微信支付服务接口
 */
public interface WxPayService {

    /**
     * 生成微信支付二维码（统一下单）
     *
     * @param orderId   订单号
     * @param money     金额（分）
     * @param notifyUrl 回调地址
     * @return
     */
    public Map createNative(String orderId, Integer money, String notifyUrl) throws Exception;

}
