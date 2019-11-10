package com.qingcheng.pojo.system;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * role实体类
 *
 * @author Administrator
 */
@Table(name = "tb_role")
@Data
public class Role implements Serializable {

    @Id
    private Integer id;//ID

    private String name;//角色名称
}
