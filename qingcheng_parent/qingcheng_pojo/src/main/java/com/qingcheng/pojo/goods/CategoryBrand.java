package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Author: Feiyue
 * Date: 2019/8/5 17:05
 * Desc: 对应db_goods库tb_category_brand表      手动创建的该类
 */
@Table(name = "tb_category_brand") //JPA注解
public class CategoryBrand implements Serializable {

    /**
     * 分类ID
     */
    @Id
    private Integer categoryId;

    /**
     * 品牌ID
     */
    @Id
    private Integer brandId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}
