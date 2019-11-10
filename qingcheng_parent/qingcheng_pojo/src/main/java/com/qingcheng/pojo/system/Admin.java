package com.qingcheng.pojo.system;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * admin实体类
 *
 * @author Administrator
 */
@Table(name = "tb_admin")
@Data
public class Admin implements Serializable {

    @Id
    private Integer id;//id

    private String loginName;//用户名

    private String password;//密码

    private String status;//状态
}
