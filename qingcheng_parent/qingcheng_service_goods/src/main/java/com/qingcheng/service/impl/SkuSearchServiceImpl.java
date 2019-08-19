package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/19 12:57
 * Desc: 商品搜索服务接口的实现类
 */
@Service // dubbo注解，注册服务
public class SkuSearchServiceImpl implements SkuSearchService {

    // 搜索业务，需要从 elastcisearch 中搜索
    /*
    java中使用es的四大步骤：
        1）创建restClient客户端
        2）封装请求对象
        3）获取响应结果
        4）关闭连接
     */


    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 商品关键字搜索
     * 青橙的做法是，将"关键字"与sku的name字段即"商品名称"，对应起来。
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map keywordsSearch(Map<String, String> searchMap) throws IOException {
        /*
        创建“双层Map夹List”结构
        Map<String, Object> innerMap = new HashMap<>(); // 最内层Map，对应一个一个sku商品
        List<Map<String, Object>> middleList = new ArrayList<>();// 中间层List，对应查询结果的所有sku商品
        Map<String, List<Map<String, Object>>> outerMap = new HashMap<>();// 最外层Map，商品查询的整个结果作为一个对象

        实际应用中根据应用场景，各层对象取名不同，如此处：
            Map<String,List> resultMap = new HashMap<String,List>();//最外层Map
            List<Map> skuList = new ArrayList<Map>();//中间层List
            Map<String, Object> skuMap = hit.getSourceAsMap(); //查询到的一个一个sku
         */

        // 创建最外层Map，用来查询结果的返回
        Map<String, List> resultMap = new HashMap<String, List>();
//        Map resultMap = new HashMap();


        // ================================封装请求对象====================================
        SearchRequest searchRequest = new SearchRequest("sku");// 索引
        searchRequest.types("doc");// 设置类型
        // 创建搜索内容构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 创建查询构建器
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();// bool查询构建器

        //----------------------------------关键字搜索-----------------------------------
        // 搜索条件构建   keywords是前后端约定的，前端输入的查询"关键字"
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
        boolQueryBuilder.must(matchQueryBuilder);// "与" 查询
        // 查询
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        //==================================封装查询结果 ===================================
        //--------从elasticsearch中查询数据--------
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);// search()方法的异常向上抛
        SearchHits searchHits = searchResponse.getHits();// 查询结果数据
        long totalHits = searchHits.getTotalHits();//总文档数
        SearchHit[] hits = searchHits.getHits();// 文档列表数组
        //--------查询结果封装---------------------
        List<Map<String, Object>> skuList = new ArrayList<>();//中间层List
        //遍历查询结果集
        for (SearchHit hit : hits) {
            Map<String, Object> skuMap = hit.getSourceAsMap(); // 查询到的一个一个sku
            System.out.println(skuMap);
            // 存入skuList
            skuList.add(skuMap);
        }
        resultMap.put("skuList", skuList);

        // 返回查询结果
        return resultMap;
    }
}
