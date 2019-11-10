package com.qingcheng.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 * @param <T>
 */
@Data // lombok注解，自动添加getter/setter方法、toString()等方法
@AllArgsConstructor // lombok注释，自动添加全参构造器
public class PageResult<T> implements Serializable {

    private Long total;//返回记录总数
    private List<T> rows;//分页查询结果
}
