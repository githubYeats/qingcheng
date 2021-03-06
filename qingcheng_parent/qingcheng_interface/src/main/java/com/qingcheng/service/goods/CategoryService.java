package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Category;

import java.util.*;

/**
 * category业务逻辑层
 */
public interface CategoryService {


    public List<Category> findAll();


    public PageResult<Category> findPage(int page, int size);


    public List<Category> findList(Map<String,Object> searchMap);


    public PageResult<Category> findPage(Map<String,Object> searchMap,int page, int size);


    public Category findById(Integer id);

    public void add(Category category);


    public void update(Category category);


    public void delete(Integer id);

    /**
     * 查询所有商品分类项    Map<String, String>        Map<"name", "分类名">
     * @return
     */
    public List<Map<String, String>> findCategoryTree();

    /**
     * 将商品分类树存入Redis缓存
     */
    public void saveCategoryTree2Redis();
}
