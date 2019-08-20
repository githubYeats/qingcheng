package com.qingcheng.dao;

import com.qingcheng.pojo.goods.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {

    /**
     * 从数据库查询某个商品分类下的规格信息：规格名称 + 规格选项
     * tb_spec表的name字段值与options字段值
     * @param categoryName 查询条件：商品分类名称，tb_category表的name字段值
     * @return
     */
    @Select("select t1.name name, t1.options options " +
            "from tb_spec t1, tb_category t2 " +
            "where t1.template_id=t2.template_id and t2.name=#{name} " +
            "group by t1.name "+
            "order by t2.seq;")
    public List<Map> findSpecByCategoryName(@Param("name") String categoryName);

}
