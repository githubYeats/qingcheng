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

    /**
     * 回调业务处理：签名验证，更新订单，记录订单日志
     *
     * @param xml 支付后响应的xml数据
     */
    public void notifyLogic(String xml) throws Exception;

    /**
     * 查询订单状态，是否已支付
     *
     * @param orderId
     * @return
     */
    public Map orderQuery(String orderId) throws Exception;

    /**
     * 订单关闭
     * 超时未支付的订单，要关闭。
     * 微信支付官方提示：“注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。”
     * @param orderId
     */
    public Map closeOrder(String orderId) throws Exception;

}
