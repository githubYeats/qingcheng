package com.qingcheng.pojo.goods;

import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/8/4 22:58
 * Desc: spu与sku的联合实体类--商品类     无数据库表与之对应
 */
@Data
public class Goods implements Serializable {
    /**
     * 一个品类的产品，对应一个spu
     */
    private Spu spu;

    /**
     * 一个品类的产品，会有多个sku
     */
    private List<Sku> skuList;
}
