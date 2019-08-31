package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.Config;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.qingcheng.service.order.WxPayService;
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
}
