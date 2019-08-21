package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.dao.SpecMapper;
import com.qingcheng.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
        //Map<String, List> resultMap = new HashMap<String, List>();
        Map resultMap = new HashMap();//它可以存放更为丰富的内容，或者把泛型中的List改变成Object


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
        /*-----------以上内容，相当于构建起elasticsearch中查询语法里的“query”--------------
            # 过滤查询 filter
            GET /sku/doc/_search
            {
              "query": {
                "bool": {
                  "filter":{
                     "match": {
                        "brandName": "华为"
                    }
                  }
                }
              },
              "from": 5,
              "size": 2
            }
         */
        //-----------------------------------------------------------------------------

        // 分页查询
        int pageNo = Integer.parseInt(searchMap.get("pageNo"));
        int pageSize = 10; // 自已设置
        int startIndex = (pageNo - 1) * pageSize;
        searchSourceBuilder.from(startIndex);
        searchSourceBuilder.size(pageSize);

        // 搜索结果排序
        String sortField = searchMap.get("sortField");// 排序字段
        String order = searchMap.get("order");// 排序规则
        if (!"".equals(sortField)) {// 不为空字符串时，再进行排序
            searchSourceBuilder.sort(sortField, SortOrder.valueOf(order));
        }

        // 搜索关键字高亮
        /*
            GET /sku/_search
            {
              "query": {
                "match": {
                  "name": "三星手机"
                }
              },
              "highlight": {
                "fields": {
                  "name": {
                    "pre_tags": "<font stype='color:red'",
                    "post_tags": "</font>"
                  },
                  "brandName": {}
                }
              }
            }

            查询结果中的高亮对象部分：
            "highlight" : {
              "name" : [
                "<font style='color:red'>三星</font> ....<font stype='color:red'>手机</font> 双卡双待"
              ]
        }
         */
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<font style='color:red'>").postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);


        searchRequest.source(searchSourceBuilder);

        // 聚合查询 ，统计关键字查询结果中有哪些商品分类
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(aggregationBuilder);

        //上面是封装请求对象
        //##########################################################################################################
        //下面是封装查询结果

        //==================================封装查询结果 ===================================
        //--------从elasticsearch中查询数据--------
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);// search()方法的异常向上抛
        SearchHits searchHits = searchResponse.getHits();// 查询结果数据
        long totalHits = searchHits.getTotalHits();//总文档数
        SearchHit[] hits = searchHits.getHits();// 文档列表数组(里面存放的就是一个一个的es文档，即一条一条数据记录)

        //----------商品列表------------------
        List<Map<String, Object>> skuList = new ArrayList<>();//中间层List
        //遍历查询结果集
        for (SearchHit hit : hits) {// hit:1个es文档（document），即一条一条数据记录。
            Map<String, Object> skuMap = hit.getSourceAsMap(); // 查询到的一个一个sku
            //System.out.println(skuMap);

            //--------添加高亮效果（关键字搜索后，展示出来的商品名称中，对应的关键字要高亮显示）--------
            /*
            高亮效果如何添加？ 关键字原来是无格式输出，现在利用es高亮搜索技术，使用带格式的文本输出。
             */
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");// name就是搜索语法中要进行高亮的字段名称
            // 获取name字段值中需要高亮显示的信息片段
            /*
            "highlight" : {
              "name" : [
                "<font stype='color:red'>三星</font>。。。。<font stype='color:red'>手机</font> 双卡双待"
              ]
             */
            Text[] fragments = name.fragments();// Text[]数组就对应着hightlight对象的name属性对应的值对象（一个数组）
            // 用高亮内容替换原无格式的内容
            /*
            “原无格式的内容”指什么？-->每一个文档（即hit对象）中name属性值，即一个个sku商品的名称。
                 Map<String, Object> skuMap = hit.getSourceAsMap(); // 查询到的一个一个sku
            fragments对象数组中的元素都是Text对象，存回skuMap中时，必须将其转回字符串。
                如果不加toString()，会报一个错误，说是org.elasticsearch.common.text.Text类必须要去实现序列化接口 Serilizable。
                    public final class Text implements Comparable<Text>, ToXContentFragment
                    这是thymeleaf中的类，经过千锤百炼的，既然没有去实现 Serilizable 接口，那肯定人家是没有问题的。
             */
            skuMap.put("name",fragments[0].toString());//fragments[0]，从上面可见，fragments数组中只有一个元素。


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

        //-------分页查询之“分页条页码渲染”------------
        /*
        服务器端要通过前端传递的当前页面pageNo，进行查询，将总页数返回给模板页面。
        然后模板页面，借助总页数，来对分页条的页码进行渲染！

        后台根据什么来计算出搜索结果应该展示的“总页数”呢？
            总页数 =  function(总记录条数, 每页记录数)。
                总记录条数，是elasticsearch中查询出来的；
                每页记录数，这是自己设置的。
         */
        long totalCount = totalHits; // long totalHits = searchHits.getTotalHits();//总文档数
        long totalPages = (totalCount % pageSize == 0) ? totalCount / pageSize : (totalCount / pageSize + 1);
        resultMap.put("totalPages", totalPages); // 存到resultMap中，给前端传递

        //-------------分页显示之“分页条数字页码显示范围控制”------------------------
        Map pageMap = new HashMap();
        long startPage = 1;
        long endPage = totalPages;
        // 条件判断
        if (totalPages > 5) {
            startPage = pageNo - 2;
            /*
            减去2后小于1，说明是第1/2/3页。
            如果当前页是第1/2/3页，那当前页码前面只有2页或小于2页，开始页自然应该是第1页。
             */
            if (startPage < 1) {
                startPage = 1;
            }
            endPage = pageNo + 2;
        }
        pageMap.put("startPage", startPage);
        pageMap.put("endPage", endPage);
        resultMap.put("pageMap", pageMap);

        // 返回查询结果给web层的controller
        return resultMap;
    }
}
