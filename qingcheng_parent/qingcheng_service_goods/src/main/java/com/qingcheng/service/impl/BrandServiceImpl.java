package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.BrandMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Brand;
import com.qingcheng.service.goods.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service //Dubbo注解，不能导成了Spring原生的@Service注解
public class BrandServiceImpl implements BrandService {

    @Autowired //Spring注解，按类型注入Bean对象
    private BrandMapper brandMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Brand> findAll() {
        //int a =1/0;//制造异常，测试统一异常处理类
        return brandMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Brand> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Brand> brands = (Page<Brand>) brandMapper.selectAll();
        return new PageResult<Brand>(brands.getTotal(),brands.getResult());
    }

    /*// 直接使用PageHelper做分页查询也可以
    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {
        //单纯分页查询
        PageHelper.startPage(page, size);
        List<Brand> brandList = brandMapper.selectAll();
        return new PageInfo<Brand>(brandList);
    }*/

    /**
     * 品牌条件查询（不加分页效果）
     *
     * @param searchMap 查询条件，Brand对象中的属性
     * @return
     */
    @Override
    public List<Brand> findList(Map<String, Object> searchMap) {
        // 生成查询条件的Example对象
        Example example = createExample(searchMap);
        // 查询，并返回
        List<Brand> brandList = brandMapper.selectByExample(example);
        return brandList;
    }

    /**
     * 品牌条件查询 + 分页展示
     *
     * @param searchMap 查询条件
     * @param page      当前页码
     * @param size      每页显示条数
     * @return
     */
    public PageResult<Brand> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Brand> brands = (Page<Brand>) brandMapper.selectByExample(example);
        return new PageResult<Brand>(brands.getTotal(),brands.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增品牌
     *
     * @param brand
     */
    @Override
    public void add(Brand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 品牌修改
     * @param brand
     * @return
     */
    @Override
    public int update(Brand brand) {
        int i = brandMapper.updateByPrimaryKeySelective(brand);
        return i;
    }

    /**
     * 根据id删除品牌
     *
     * @param id
     * @return 执行成功，返回1；否则返回0
     */
    @Override
    public int delete(Integer id) {
        int i = brandMapper.deleteByPrimaryKey(id);
        return i;
    }

    /**
     * 构造查询条件
     *
     * @param searchMap 前端输入的查询条件，封装成Map对象
     * @return 通用Mapper体系下，封装后的查询条件
     */
    private Example createExample(Map<String, Object> searchMap){
        // 定义查询对象
        /*
        public Example(Class<?> entityClass) {
            this(entityClass, true);
        }
         */
        Example example=new Example(Brand.class);

        // 创建条件构造器，并进行条件构造
        /*
        criteria：(评判或作决定的)标准，准则，原则
         */
        Example.Criteria criteria = example.createCriteria();

        //条件构建
        if(searchMap!=null){
            // 品牌名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 品牌图片地址
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 品牌的首字母
            if(searchMap.get("letter")!=null && !"".equals(searchMap.get("letter"))){
                criteria.andLike("letter","%"+searchMap.get("letter")+"%");
            }

            // 品牌id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }

        }
        return example;
    }

}
