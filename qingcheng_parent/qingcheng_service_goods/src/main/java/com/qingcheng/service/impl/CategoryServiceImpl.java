package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.CategoryMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Category> findAll() {
        return categoryMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Category> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectAll();
        return new PageResult<Category>(categorys.getTotal(), categorys.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Category> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Category> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Category> categorys = (Page<Category>) categoryMapper.selectByExample(example);
        return new PageResult<Category>(categorys.getTotal(), categorys.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Category findById(Integer id) {
        return categoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param category
     */
    public void add(Category category) {
        categoryMapper.insert(category);
    }

    /**
     * 修改
     *
     * @param category
     */
    public void update(Category category) {
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        //判断当前分类是否有子分类
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId", id);
        //根据查询条件，进行数量的查询
        int count = categoryMapper.selectCountByExample(example);
        if (count > 0) {
            //有下一级分类，不能删除
            throw new RuntimeException("当前商品分类存在下一级分类，不能删除！");
        }

        categoryMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 分类名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 是否显示
            if (searchMap.get("isShow") != null && !"".equals(searchMap.get("isShow"))) {
                criteria.andEqualTo("isShow", searchMap.get("isShow"));
            }
            // 是否导航
            if (searchMap.get("isMenu") != null && !"".equals(searchMap.get("isMenu"))) {
                criteria.andLike("isMenu", "%" + searchMap.get("isMenu") + "%");
            }

            // 分类ID
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 商品数量
            if (searchMap.get("goodsNum") != null) {
                criteria.andEqualTo("goodsNum", searchMap.get("goodsNum"));
            }
            // 排序
            if (searchMap.get("seq") != null) {
                criteria.andEqualTo("seq", searchMap.get("seq"));
            }
            // 上级ID
            if (searchMap.get("parentId") != null) {
                criteria.andEqualTo("parentId", searchMap.get("parentId"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }

        }
        return example;
    }

    /**
     * 查询所有商品分类项    1级/2/级/3级，各级分类项     递归查询
     *
     * @return
     */
    @Override
    public List<Map<String, String>> findCategoryTree() {
        // 查询思路：先查询出所有分类，然后递归查询出1级/2级/3级分类并封装成Map对象，然后给前端返回

        // ----------------构造查询条件，查询出所有1级分类------------
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        // is_show字段=1   显示     模板查询条件中的模糊查询要改为精确查询
        criteria.andEqualTo("isShow", "1");
        // 设置排序
        example.setOrderByClause("seq");
        // 查数据库表
        List<Category> categoryList = categoryMapper.selectByExample(example);
        System.out.println(categoryList);

        // 递归查询
        List<Map<String, String>> mapList = findCategoriesByParentId(categoryList, 0);

        return mapList;
    }

    private List<Map<String, String>> findCategoriesByParentId(List<Category> categoryList, Integer parentId) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getParentId().equals(parentId)) {
                System.out.println(category);
                Map map = new HashMap();
                map.put("name", category.getName());
                map.put("categories", findCategoriesByParentId(categoryList, category.getId()));
                mapList.add(map);
            }
        }
        return mapList;
    }

}
