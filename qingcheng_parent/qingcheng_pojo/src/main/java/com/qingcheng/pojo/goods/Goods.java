package com.qingcheng.pojo.goods;

import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/8/4 22:58
 * Desc: spu与sku的联合实体类--商品类     无数据库表与之对应
 */
public class Goods implements Serializable {
    /**
     * 一个品类的产品，对应一个spu
     */
    private Spu spu;

    /**
     * 一个品类的产品，会有多个sku
     */
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
