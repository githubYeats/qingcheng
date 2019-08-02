package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Brand;

import java.util.*;

/**
 * brand业务逻辑层       qingcheng_goods库，tb_brand表
 */
public interface BrandService {

    /**
     * 查询所有品牌的信息
     * @return
     */
    public List<Brand> findAll();

    /**
     * 品牌查询 + 分页展示
     * @param page
     * @param size
     * @return
     */
    public PageResult<Brand> findPage(int page, int size);

    /**
     * 品牌条件查询（不加分页效果）
     * @param searchMap 查询条件，Brand对象中的属性
     * @return
     */
    /*
    Map<String, Object> Brand类中属性的类型不止一种，因此要用统一的Object类型来接收
     */
    public List<Brand> findList(Map<String,Object> searchMap);

    /**
     * 品牌条件查询 + 分页展示
     * @param searchMap 查询条件，Brand对象中的属性
     * @return
     */
    public PageResult<Brand> findPage(Map<String,Object> searchMap,int page, int size);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public Brand findById(Integer id);

    /**
     * 新增一个品牌信息
     * @param brand
     */
    public void add(Brand brand);

    /**
     * 更新品牌信息：根据id修改
     * @param brand
     * @return
     */
    public int update(Brand brand);

    /**
     * 根据id删除品牌
     * @param id
     * @return 执行成功，返回1；否则返回0
     */
    public int delete(Integer id);

}
