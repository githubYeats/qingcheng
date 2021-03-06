package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.Config;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.qingcheng.service.order.OrderService;
import com.qingcheng.service.order.WxPayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/31 11:04
 * Desc:
 */
@Service // dubbo注解
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private Config config;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 生成二维码
     *
     * @param orderId   订单号
     * @param money     金额（分）
     * @param notifyUrl 回调地址
     * @return
     */
    @Override
    public Map createNative(String orderId, Integer money, String notifyUrl) throws Exception {
        // 1.封装请求参数
        /*
        查看接口文档中请求参数封装的要求：
            https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
                API列表-->统一下单-->请求参数
                将必须项进行封装即可
         */
        Map<String, String> map = new HashMap<>();
        map.put("appid", config.getAppID());//公众账号ID
        map.put("mch_id", config.getMchID());//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        //map.put("sign", "32bit");//32位的，运行时，wxpay会自动添加签名
        map.put("body", "青橙");//商品描述
        map.put("out_trade_no", orderId);//商户订单号，单个商家内必须唯一。 每次运行测试，都要换个值。
        map.put("total_fee", String.valueOf(money));//标价金额
        map.put("spbill_create_ip", "127.0.0.1");//终端IP
        //通知地址
        //异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        map.put("notify_url", notifyUrl);//通知地址
        map.put("trade_type", "NATIVE");//交易类型
        // 生成xml格式的参数
        String xmlParam = WXPayUtil.generateSignedXml(map, config.getKey());
        System.out.println("参数：" + xmlParam);

        //2.发送请求
        WXPayRequest wxPayRequest = new WXPayRequest(config);
        String xmlResult = wxPayRequest.requestWithCert("/pay/unifiedorder",
                null, xmlParam, false);
        System.out.println("结果：" + xmlResult);

        //3.解析返回结果
        Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
        // 封装订单关键信息
        Map result = new HashMap();
        result.put("code_url", mapResult.get("code_url"));//回调地址
        result.put("total_fee", String.valueOf(money));//订单金额
        result.put("out_trade_no", orderId);//订单号
        return result;
    }

    /**
     * 下单成功，回调业务处理：签名验证，更新订单状，记录订单日志
     *
     * @param xml 支付后响应的xml数据
     * @return
     */
    @Override
    public void notifyLogic(String xml) throws Exception {
        // xml-->map    转成map，便于提取字段
        Map<String, String> map = WXPayUtil.xmlToMap(xml);

        // 签名验证
        boolean isSignatureValid = WXPayUtil.isSignatureValid(map, config.getKey());

        // 解析xml各字段
        /*for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }*/

        // 解析以下字段
        /*
        out_trade_no		订单id号		在对应的商家中，其值是唯一的。
        sign				签名			支付平台自动添加的签名，用于验证交易的合法性。
        transaction_id		交易流水号		支付交易，由支付平台返回的交易流水号，在整个支付平台中是唯一的。
        return_code		    返回状态码		SUCCESS | FAIL   此字段是通信标识，非交易标识，交易是否成功需要查看
                                            result_code来判断
        result_code		业务结果		支付结果状态码	SUCCESS | FAIL  SUCCESS表示支付成功
         */
        String out_trade_no = map.get("out_trade_no");
        String sign = map.get("sign");
        String transaction_id = map.get("transaction_id");
        String return_code = map.get("return_code");
        String result_code = map.get("result_code");
        System.out.println(out_trade_no);
        System.out.println(sign);
        System.out.println(transaction_id);
        System.out.println(return_code);
        System.out.println(result_code);

        // 更新订单状态，记录订单日志
        if (isSignatureValid && "SUCCESS".equals(map.get("result_code"))) {
            orderService.updateOrderAndLog(map.get("out_trade_no"), map.get("transaction_id"));

            // 发送消息给RabbitMQ
            String orderId = map.get("out_trade_no");
            rabbitTemplate.convertAndSend("wxPayNotify", "", orderId);

        } else {
            //记录全局日志
        }
    }

    /**
     * 查询订单
     * 接口链接：https://api.mch.weixin.qq.com/pay/orderquery
     *
     * @param orderId
     * @return
     */
    @Override
    public Map orderQuery(String orderId) throws Exception {
        // 1.封装请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appid", config.getAppID());//公众账号ID
        requestMap.put("mch_id", config.getMchID());//商户号
        requestMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        requestMap.put("out_trade_no", orderId);//商户订单号
        //requestMap.put("sign", "32bit");//32位的，运行时，wxpay会自动添加签名
        String requestXml = WXPayUtil.generateSignedXml(requestMap, config.getKey());
        System.out.println("查询订单的请求参数：" + requestXml);

        // 2.调用微信支付订单查询接口，查询订单状态
        WXPayRequest wxPayRequest = new WXPayRequest(config);
        String xmlResult = wxPayRequest.requestWithCert("/pay/orderquery",
                null, requestXml, false);
        System.out.println("订单查询的返回结果：" + xmlResult);

        // 3.解析返回结果
        Map<String, String> responseMap = WXPayUtil.xmlToMap(xmlResult);
        // 封装关键字段
        Map resultMap = new HashMap();
        resultMap.put("return_code", responseMap.get("return_code"));
        resultMap.put("return_msg", responseMap.get("return_msg"));
        //业务结果，交易是否成功需要查看trade_state来判断
        if (xmlResult.contains("result_code")) {
            resultMap.put("result_code", responseMap.get("result_code"));
        }
        //交易状态
        /*
        SUCCESS—支付成功    REFUND—转入退款     NOTPAY—未支付      CLOSED—已关闭      REVOKED—已撤销（付款码支付）
        USERPAYING--用户支付中（付款码支付）    PAYERROR--支付失败(其他原因，如银行返回失败)
         */
        if (xmlResult.contains("trade_state")) {
            resultMap.put("trade_state", responseMap.get("trade_state"));
        }
        // 订单号
        if (xmlResult.contains("out_trade_no")) {
            resultMap.put("out_trade_no", responseMap.get("out_trade_no"));
        }
        // 支付完成时间
        if (xmlResult.contains("time_end")) {
            resultMap.put("time_end", responseMap.get("time_end"));
        }
        // 交易流水号
        if (xmlResult.contains("transaction_id")) {
            resultMap.put("transaction_id", responseMap.get("transaction_id"));
        }

        return resultMap;
    }

    /**
     * 订单关闭
     * 超时未支付的订单，要关闭。
     * 微信支付官方提示：“注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。”
     * <p>
     * 接口链接：https://api.mch.weixin.qq.com/pay/closeorder
     *
     * @param orderId
     */
    @Override
    public Map closeOrder(String orderId) throws Exception {
        // 1.封装请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appid", config.getAppID());//公众账号ID
        requestMap.put("mch_id", config.getMchID());//商户号
        requestMap.put("out_trade_no", orderId);//商户订单号
        requestMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        //requestMap.put("sign", "32bit");//32位的，运行时，wxpay会自动添加签名
        String requestXml = WXPayUtil.generateSignedXml(requestMap, config.getKey());
        System.out.println("关闭订单的请求参数：" + requestXml);

        // 2.发送请求
        WXPayRequest wxPayRequest = new WXPayRequest(config);
        Thread.sleep(300000);//延迟5分钟
        // 请求关闭订单
        String xmlResult = wxPayRequest.requestWithCert("/pay/closeorder",
                null, requestXml, false);
        System.out.println("关闭订单的返回结果：" + xmlResult);

        // 3.解析返回结果
        Map<String, String> responseMap = WXPayUtil.xmlToMap(xmlResult);
        // 封装关键字段
        Map resultMap = new HashMap();
        resultMap.put("return_msg", responseMap.get("return_msg"));
        resultMap.put("return_code", responseMap.get("return_code"));
        //业务结果，交易是否成功需要查看trade_state来判断
        if (xmlResult.contains("result_code")) {
            resultMap.put("result_code", responseMap.get("result_code"));
        }
        // 返回信息
        if (xmlResult.contains("result_msg")) {
            resultMap.put("result_msg", responseMap.get("result_msg"));
        }
        return resultMap;
    }
}
