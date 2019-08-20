package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 从数据库查询某一商品分类下的所有品牌信息
     * 查询品牌名称与品牌图片
     *
     * @param categoryName
     * @return
     */
    @Select("select t1.name name, t1.image image  " +
            "from tb_brand t1, tb_category_brand t2, tb_category t3 " +
            "where t1.id=t2.brand_id and t3.id=t2.category_id and t3.name=#{name};")
    public List<Map> findBrandByCategoryName(@Param("name") String categoryName);
}
