package com.qingcheng.factory;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Author: Feiyue
 * Date: 2019/8/19 10:42
 * Desc: elasticsearch的RestClient工厂类(相当于一个工具类)    用于生成restClient客户端对象
 */
public class RestClientFactory {
    /*
    java中使用es的四大步骤：
        1）创建restClient客户端
        2）封装请求对象
        3）获取响应结果
        4）关闭连接
     */

    /**
     * 创建restClient客户端
     * @return
     */
    public static RestHighLevelClient getRestHighLevelClient(String hostname,int port,String scheme) {
        //HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        HttpHost httpHost = new HttpHost(hostname, port, scheme);
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);//rest构建器
        return new RestHighLevelClient(restClientBuilder);//高级客户端对象
    }
}
