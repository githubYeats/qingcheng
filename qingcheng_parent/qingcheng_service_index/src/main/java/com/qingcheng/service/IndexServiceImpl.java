package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.service.index.IndexService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/30 18:44
 * Desc:
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Reference
    private SpuService spuService;

    /**
     * 将spuId对应的所有sku添加到es中
     *
     * @param spuId
     * @throws IOException
     */
    @Override
    public void addData2ESBySpuId(String spuId) throws IOException {
        //------------------------------- 1.连接Rest接口---------------------------------
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);//rest构建器
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);//高级客户端对象

        // -------------------------------2.封装请求对象------------------------------------

        BulkRequest bulkRequest = new BulkRequest();

        // 获取商品对象 goods
        Goods goods = spuService.findGoodsById(spuId);
        // 获取sku列表
        List<Sku> skuList = goods.getSkuList();

        int count = 0;
        for (Sku sku : skuList) {
            // 创建索引结构   public IndexRequest(String index, String type, String id)
            IndexRequest indexRequest = new IndexRequest("sku", "doc", sku.getId());
            // 创建文档document
            Map skuMap = new HashMap();
            //skuMap.put("id",sku.getId());
            skuMap.put("spuId", sku.getSpuId());
            skuMap.put("image", sku.getImage());
            skuMap.put("name", sku.getName());
            skuMap.put("brandName", sku.getBrandName());
            skuMap.put("categoryName", sku.getCategoryName());
            skuMap.put("price", sku.getPrice());
            // 创建时间
            skuMap.put("createTime", sku.getCreateTime());
            skuMap.put("saleNum", sku.getSaleNum());
            skuMap.put("commentNum", sku.getCommentNum());
            // 添加规格参数    对象
            JSONObject jsonObject = JSON.parseObject(sku.getSpec());
            skuMap.put("spec", jsonObject);
            // 添加文档到es
            indexRequest.source(skuMap);
            bulkRequest.add(indexRequest);//可多次添加

            System.out.println("成功插入第" + (count++) + "条数据！");
        }

        //----------------------------3.获取响应结果---------------------------------
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        int status = bulkResponse.status().getStatus();//http响应状态码
        System.out.println(status);

        //----------------------------4.关闭连接------------------------------------
        restHighLevelClient.close();
    }

    /**
     * 根据spuId删除es中的sku
     *
     * @param spuId
     */
    @Override
    public void deleteDataFormESBySpuId(String spuId) throws IOException {
        /*
        RestHighLevelClient
            public final DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options)
                DeleteRequest
                    public DeleteRequest(String index, String type, String id)
         */

        //------------------------------- 1.连接Rest接口---------------------------------
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);//rest构建器
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);//高级客户端对象

        // 获取商品对象 goods
        Goods goods = spuService.findGoodsById(spuId);
        // 获取sku列表
        List<Sku> skuList = goods.getSkuList();
        String _index = "sku";
        String _type = "doc";
        for (Sku sku : skuList) {//根据文档的id删除
            DeleteRequest deleteRequest = new DeleteRequest(_index, _type, sku.getId());
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        }
        restHighLevelClient.close();
    }

    /**
     * 接收监听类传递的索引消息，并处理
     *
     * @param body
     * @throws IOException
     */
    @Override
    public void indexMsgDeal(byte[] body) throws IOException {
        String spuId = new String(body);
        Spu spu = spuService.findById(spuId);
        if ("1".equals(spu.getIsMarketable())) {//上架商品
            addData2ESBySpuId(spuId);
        }
        if ("0".equals(spu.getIsMarketable())) {//下架商品
            deleteDataFormESBySpuId(spuId);
        }
    }
}
