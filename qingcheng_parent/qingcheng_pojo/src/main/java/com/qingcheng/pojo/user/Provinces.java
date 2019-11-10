package com.qingcheng.pojo.user;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * provinces实体类
 *
 * @author Administrator
 */
@Table(name = "tb_provinces")
@Data
public class Provinces implements Serializable {

    @Id
    private String provinceid;//省份ID

    private String province;//省份名称
}
