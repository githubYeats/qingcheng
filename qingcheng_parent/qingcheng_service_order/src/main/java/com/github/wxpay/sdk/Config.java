package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * Author: Feiyue
 * Date: 2019/8/31 10:54
 * Desc:
 */
public class Config extends WXPayConfig {
    @Override
    public String getAppID() {
        return "wx8397f8696b538317";//APP的唯一标识
    }

    @Override
    public String getMchID() {
        return "1473426802";//商户号
    }

    @Override
    public String getKey() {
        return "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb";//商户密钥
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            public void report(String domain, long elapsedTimeMillis, Exception ex) {
            }

            public DomainInfo getDomain(WXPayConfig config) {
                return new DomainInfo("api.mch.weixin.qq.com", true);
            }
        };
    }
}
