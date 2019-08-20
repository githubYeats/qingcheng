package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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


    /**
     * es高级客户端
     */
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 品牌过滤搜索时使用
     */
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 规格过滤搜索时使用
     */
    @Autowired
    private SpecMapper specMapper;


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

        // 关键字搜索
        // 搜索条件构建   keywords是前后端约定的，前端输入的查询"关键字"
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keywords"));
        boolQueryBuilder.must(matchQueryBuilder);// "与" 查询

        // 商品分类过滤
        if (null != searchMap.get("category")) {// 搜索条件不为空时，再执行
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 品牌过滤
        if (null != searchMap.get("brand")) {// 搜索条件不为空时，再执行
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
            boolQueryBuilder.filter(termQueryBuilder);
        }

        // 规格过滤
        if (null != searchMap.get("spec")) {// 搜索条件不为空时，再执行
            for (String key : searchMap.keySet()) {
                // 前后端约定：所有spec.开头的参数才是规格
                if (key.startsWith("spec.")) {
                    /*
                    (1)是规格参数，再进行查询
                    (2)termQuery(key+".keyword", searchMap.get(key))，需要给key加上“.keyword”，是es中对于object类型的字段
                        进行精确查询所要求的。  es中keyword类型的数据可以进行精确查询
                     */
                    TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(key + ".keyword", searchMap.get(key));
                    boolQueryBuilder.filter(termQueryBuilder);
                }
            }
        }

        // 价格过滤
        /*
        价格区间
            0-499 500-999 1000-1699 1700-2799 2800-4499 4500-11999 12000-*
         */
        if (null != searchMap.get("price")) {// 有价格过滤条件时，再执行过滤
            String[] prices = searchMap.get("price").split("-");// price=2800-4499  [价格下限，价格上限]

            // 下限不为0，即0-499区间之外的其他区间。
            /*
            下限不为0，即有下限，此时需要查询条件设置下限，即要"大于等于"下限
                0就不是价格下限了吗？
                    商品的价格不会比0还小，甚至等于0的可能都几乎没有，不然商家尽亏本了。
             */
            if (!prices[0].equals("0")) {
                // 前端传递过来的价格，单位是“元”。后台查询出来的价格单位是“分”。   需要保持前后端一致才可以。
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(prices[0] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }

            // 上限不为*，即12000-*区间之外的其他区间
            /*
            上限不为*，即有价格上限，要为过滤条件设置价格上限。
             */
            if (!prices[1].equals("*")) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lte(prices[1] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        // 聚合查询 ，统计关键字查询结果中有哪些商品分类
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(aggregationBuilder);


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
            //System.out.println(skuMap);
            // 存入skuList
            skuList.add(skuMap);
        }
        resultMap.put("skuList", skuList);

        // --------商品分类列表---------
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        Terms terms = (Terms) aggregationMap.get("sku_category");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<String> categoryNameList = new ArrayList<>();// 存放聚合查询得出的商品分类信息
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();// 商品分类名称
            categoryNameList.add(key);
        }
        resultMap.put("categoryNameList", categoryNameList);

        // 商品分类名称构建（品牌过滤/规格过滤/价格过滤，都要用到此名称作为查询条件）
        String categoryName = "";
        // 前置条件判断
        if (searchMap.get("category") == null) {// 用户没有点击分类名称时
            if (categoryNameList.size() > 0) {//有分类时
                categoryName = categoryNameList.get(0);
            }
        } else {//用户点击了某一商品分类时
            categoryName = searchMap.get("category");
        }

        // --------品牌列表-------------
        // 查询某一商品分类下的所有品牌信息
        if (null == searchMap.get("brand")) {// 没有品牌过滤条件时，再执行

            List<Map> brandList = brandMapper.findBrandByCategoryName(categoryName);
            resultMap.put("brandList", brandList);
        }

        //--------规格列表------------------
        // 查询某一商品分类的规格信息
        if (null == searchMap.get("spec")) {
            List<Map> specList = specMapper.findSpecByCategoryName(categoryName);
            // spec是一个对象，options值格式为：20英寸,50英寸,60英寸。需要将其处理成map，这样前端才能取值
            for (Map spec : specList) {
               /* String options = (String) spec.get("options");
                String[] stringArr = options.split(",");
                spec.put("options", stringArr);// 覆盖原先的逗号分隔的字符串数据*/
                String[] stringArr = ((String) spec.get("options")).split(",");
                spec.put("options", stringArr);

            }
            resultMap.put("specList", specList);
        }

        // 返回查询结果
        return resultMap;
    }
}
