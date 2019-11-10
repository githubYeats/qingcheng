package com.qingcheng.pojo.goods;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * brand实体类
 */
@Table(name = "tb_brand") //JPA注解，将此实体类映射到表tb_brand
@Data
public class Brand implements Serializable {
    @Id //JPA注解，指明主键
    private Integer id;//品牌id

    private String name;//品牌名称

    private String image;//品牌图片地址

    private String letter;//品牌名首字母
    
    private int seq;//排序
}
