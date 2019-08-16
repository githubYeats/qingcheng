package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;

import java.util.*;

/**
 * sku业务逻辑层
 */
public interface SkuService {


    public List<Sku> findAll();


    public PageResult<Sku> findPage(int page, int size);


    public List<Sku> findList(Map<String,Object> searchMap);


    public PageResult<Sku> findPage(Map<String,Object> searchMap,int page, int size);


    public Sku findById(String id);

    public void add(Sku sku);


    public void update(Sku sku);


    public void delete(String id);

    /**
     * 缓存所有商品的的价格数据
     */
    public void saveAllPrice2Redis();

    /**
     * 查询商品价格（单位：分）
     * @param skuId
     * @return
     */
    public Integer findPriceById(String skuId);

    /**
     * 修改某商品价格数据后，对应地更新缓存数据
     * @param skuId
     * @param price
     */
    public void savePrice2RedisBySukId(String skuId,Integer price);

    /**
     * 某商品（一个sku）被下架或删除时，清除缓存中其对应的价格数据
     * @param skuId
     */
    public void deletePriceFromRedisBySkuId(String skuId);


}
