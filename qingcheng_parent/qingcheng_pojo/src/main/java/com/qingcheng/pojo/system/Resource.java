package com.qingcheng.pojo.system;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * resource实体类
 *
 * @author Administrator
 */
@Table(name = "tb_resource")
@Data
public class Resource implements Serializable {

    @Id
    private Integer id;//id

    private String resKey;//res_key

    private String resName;//res_name

    private Integer parentId;//parent_id
}
