package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {
    /**
     * 商品库存扣减
     *
     * @param skuId sku的id号
     * @param num   扣减数量
     */
    @Update("update tb_sku set num=num-#{num} where id=#{id};")
    public void stockDeduct(@Param("id") String skuId, @Param("num") Integer num);


    /**
     * 商品销售增加
     *
     * @param skuId
     * @param num   增加量
     */
    @Update("update tb_sku set saleNum=saleNum-#{num} where id=#{id}")
    public void addSaleNum(@Param("id") String skuId, @Param("num") Integer num);
}
