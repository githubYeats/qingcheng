package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpecService;
import com.qingcheng.util.CacheKey;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Sku> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();
        return new PageResult<Sku>(skus.getTotal(), skus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Sku> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return skuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Sku> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectByExample(example);
        return new PageResult<Sku>(skus.getTotal(), skus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param sku
     */
    public void add(Sku sku) {
        skuMapper.insert(sku);
    }

    /**
     * 修改
     *
     * @param sku
     */
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        skuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 商品id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 商品条码
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SKU名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 商品图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 商品图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // SPUID
            if (searchMap.get("spuId") != null && !"".equals(searchMap.get("spuId"))) {
                criteria.andLike("spuId", "%" + searchMap.get("spuId") + "%");
            }
            // 类目名称
            if (searchMap.get("categoryName") != null && !"".equals(searchMap.get("categoryName"))) {
                criteria.andLike("categoryName", "%" + searchMap.get("categoryName") + "%");
            }
            // 品牌名称
            if (searchMap.get("brandName") != null && !"".equals(searchMap.get("brandName"))) {
                criteria.andLike("brandName", "%" + searchMap.get("brandName") + "%");
            }
            // 规格
            if (searchMap.get("spec") != null && !"".equals(searchMap.get("spec"))) {
                criteria.andLike("spec", "%" + searchMap.get("spec") + "%");
            }
            // 商品状态 1-正常，2-下架，3-删除
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 价格（分）
            if (searchMap.get("price") != null) {
                criteria.andEqualTo("price", searchMap.get("price"));
            }
            // 库存数量
            if (searchMap.get("num") != null) {
                criteria.andEqualTo("num", searchMap.get("num"));
            }
            // 库存预警数量
            if (searchMap.get("alertNum") != null) {
                criteria.andEqualTo("alertNum", searchMap.get("alertNum"));
            }
            // 重量（克）
            if (searchMap.get("weight") != null) {
                criteria.andEqualTo("weight", searchMap.get("weight"));
            }
            // 类目ID
            if (searchMap.get("categoryId") != null) {
                criteria.andEqualTo("categoryId", searchMap.get("categoryId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 缓存所有商品的的价格数据
     */
    @Override
    public void saveAllPrice2Redis() {
        if (!redisTemplate.hasKey(CacheKey.SKU_PRICE)) {//缓存中没有商品价格数据，再进行缓存
            List<Sku> skuList = skuMapper.selectAll();
            for (Sku sku : skuList) {
                if ("1".equals(sku.getStatus())) {// sku商品处于正常状态。 1正常，2下架，3删除
                    redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(sku.getId(), sku.getPrice());
                }
            }
        } else {
            System.out.println("商品价格数据已缓存，无需重复加载！");
        }
    }

    /**
     * 通过skuId，从缓存中查询商品价格
     *
     * @param skuId
     * @return
     */
    @Override
    public Integer findPriceById(String skuId) {
        return (Integer) redisTemplate.boundHashOps(CacheKey.SKU_PRICE).get(skuId);
    }

    /**
     * 修改某商品价格数据后，对应地更新缓存数据
     *
     * @param skuId
     * @param price
     */
    @Override
    public void savePrice2RedisBySukId(String skuId, Integer price) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(skuId, price);
    }

    /**
     * 某商品（一个sku）被下架或删除时，清除缓存中其对应的价格数据
     *
     * @param skuId
     */
    @Override
    public void deletePriceFromRedisBySkuId(String skuId) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).delete(skuId);
    }

    /**
     * 批量插入数量到elasticsearch
     *
     * @throws IOException
     */
    @Override
    public void batchInsertData2ES() throws IOException {
        //------------------------------- 1.连接Rest接口---------------------------------
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);//rest构建器
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);//高级客户端对象

        // -------------------------------2.封装请求对象------------------------------------

        BulkRequest bulkRequest = new BulkRequest();
        List<Sku> skuList = skuMapper.selectAll();
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
     * 批量更新商品库存与销售
     *
     * @param orderItemList
     * @return
     */
    @Override
    public boolean deductStock(List<OrderItem> orderItemList) {
        // 检查是否可以扣减
        boolean isDeductable = true;//默认可以扣减
        for (OrderItem orderItem : orderItemList) {
            // 获取sku对象
            Sku sku = findById(orderItem.getSkuId());
            /*
            不能扣减的情况：
                没有此ksu | 商品下回或删除了 | 库存量<用户购买量
             */
            if (sku == null || !"1".equals(sku.getStatus()) || sku.getNum() < orderItem.getNum()) {
                isDeductable = false;
                break;
            }
        }
        if (isDeductable) {//可以扣减
            for (OrderItem orderItem : orderItemList) {
                // 扣减库存
                skuMapper.stockDeduct(orderItem.getSkuId(), orderItem.getNum());
                // 增加销量
                skuMapper.addSaleNum(orderItem.getSkuId(), orderItem.getNum());
            }
        }
        return isDeductable;
    }
}
