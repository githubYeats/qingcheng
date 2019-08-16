package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String,Object> searchMap);


    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);


    public Spu findById(String id);

    public void add(Spu spu);


    public void update(Spu spu);


    public void delete(String id);

    /**
     * 新增商品
     * @param goods
     */
    public void saveGoods(Goods goods);

    /**
     * 通过spuId查询商品
     * @param id    spuId是分布式ID，是字符串类型的
     */
    public Goods findGoodsById(String id);

    /**
     * 商品审核
     * @param id    商品spu的ID
     * @param status    审核情况    0：审核未通过     1：审核通过
     * @param message   审核留言，情况说明
     */
    public void audit(String id, String status, String message);

    /**
     * 商品下架     商品上下架是以SPU为单位的。
     * @param id    tb_spu表spu_id字段
     */
    public void pull(String id);

    /**
     * 商品上架
     * @param id    spuId
     */
    public void put(String id);

    /**
     * 批量上架
     * 需要前端传递要上架的多款商品的spuId
     * @param ids
     * @return
     */
    public int putMany(String[] ids);

    /**
     * 批量下架
     * @param ids 将要下架的多款商品的spuId
     * @return
     */
    public int pullMany(String[] ids);



}
