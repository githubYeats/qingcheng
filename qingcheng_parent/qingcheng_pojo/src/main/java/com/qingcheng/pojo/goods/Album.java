package com.qingcheng.pojo.goods;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * album实体类
 *
 * @author Administrator
 */
@Table(name = "tb_album")
@Data
public class Album implements Serializable {

    @Id
    private Long id;//编号

    private String title;//相册名称

    private String image;//相册封面

    private String imageItems;//图片列表
}
