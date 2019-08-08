package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.CategoryBrandMapper;
import com.qingcheng.dao.CategoryMapper;
import com.qingcheng.dao.SkuMapper;
import com.qingcheng.dao.SpuMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpuService.class) //duboo注解
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(), spus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     *
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andLike("isMarketable", "%" + searchMap.get("isMarketable") + "%");
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andLike("isEnableSpec", "%" + searchMap.get("isEnableSpec") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 新增商品。  会保存一个spu信息，与多个sku信息。      引入“分页式ID”：雪花算法
     *
     * @param goods
     */
    @Transactional //Spring注解，事务控制
    public void saveGoods(Goods goods) {
        //---------------------保存一个spu信息--------------------------
        /*
        对比tb_spu表字段，与前端向后台传递参数约定的格式内容。看看Spu对象缺少哪些内容
             id   spu的ID
             sale_num
             comment_num
             is_marketable
             is_enable_spec
             is_delete
             status           审核状态
         */
        Spu spu = goods.getSpu();
        if (spu.getId() == null) {//新增商品
            // id   spu的ID
            spu.setId(idWorker.nextId() + "");
            spuMapper.insert(spu);
        } else { //修改商品
            //删除原来的sku列表
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId", spu.getId());
            skuMapper.deleteByExample(example);
            //执行spu的修改
            spuMapper.updateByPrimaryKeySelective(spu);
        }

        //----------------------保存多个sku信息，即skuList-------------------------
        /*
        sku有19个字段，前端只传递9个参数，其他的要自己去设置。
         */
        // id       分布式ID解决方案
        // name     SKU名称 = SPU名称（tb_spu表name字段值） + 规格值（tb_sku表spec字段值）
        // "spec": {"颜色":"红","机身内存":"64G"}
        // 需要取出其中的各value值并添加到SPU名称后
        // create_time
        // update_time
        // spu_id
        // category_id      分类ID = tb_category表id字段值 = tb_spu表的category3_id字段值
        // category_name    分类名称 = tb_category表的name字段值
        // brand_name   品牌名称 =  tb_brand表name字段值
        // sale_num     销售量     初始时为0
        // comment_num      评论数     初始时为0
        // status   商品状态    商品状态 1-正常，2-下架，3-删除
        // 保存SKU信息到数据库

        //分类对象
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            // spu_id
            sku.setSpuId(spu.getId());
            if (sku.getId() == null) {//SKU无id时，表明是新增商品
                // id
                sku.setId(idWorker.nextId() + "");
                // create_time
                sku.setCreateTime(new Date());//创建日期
            }

            //未启用规格的sku处理
            if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                sku.setSpec("{}");
            }

            //sku名称  =spu名称+规格值列表
            String name = spu.getName();
            //sku.getSpec()  {"颜色":"红","机身内存":"64G"}
            Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (String value : specMap.values()) {
                name += " " + value;
            }
            sku.setName(name);//名称

            sku.setUpdateTime(new Date());
            sku.setCategoryId(spu.getCategory3Id());
            sku.setCategoryName(category.getName());
            sku.setCommentNum(0);
            sku.setSaleNum(0);
            skuMapper.insert(sku);
        }

        //建立分类和品牌的关联    tb_category_brand表
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setCategoryId(spu.getCategory3Id());
        categoryBrand.setBrandId(spu.getBrandId());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            categoryBrandMapper.insert(categoryBrand);
        }
    }

    /**
     * 根据商品的spuId查询商品
     * @param id    spuId是分布式ID，是字符串类型的
     * @return
     */
    @Override
    public Goods findGoodsById(String id) {
        //查询Spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //查询sku列表
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);
        //封装为组合实体类
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    /**
     * 商品审核
     *
     * @param id      tb_spu表id字段
     * @param status  tb_spu表status字段。
     *                商品审核状态
     *                0：新增未审核
     *                1：审核通过
     *                2：审核未通过     未通常必须要有说明，即message留言
     *                对商品进行审核，就是设置status字段为1还是2
     * @param message 审核留言，情况说明
     */
    @Override
    @Transactional //Spring注解，事务控制
    public void audit(String id, String status, String message) {
        // 1. 商品审核操作
        /*// 这种方式，每次都要向数据库去查询，效率比较低。
        Spu spu = spuMapper.selectByPrimaryKey(id); // 获取当前要修改的Spu对象
        spu.setStatus(status);
        spuMapper.updateByPrimaryKey(spu);*/
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);
        if ("1".equals(status)) {
            spu.setIsMarketable("1");//审核通过，自动上架

        }
        spuMapper.updateByPrimaryKeySelective(spu);

        // 2. 商品审核记录

        // 3. 审核日志功能
    }

    /**
     * 商品下架
     *
     * @param id tb_spu表spu_id字段
     */
    @Override
    public void pull(String id) {
        // 修改tb_spu表status字段值
        // 不去查询Spu对象，而是直接new
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsMarketable("0");//商品下架
        spuMapper.updateByPrimaryKeySelective(spu);

        // 商品日志     未完
    }

    /**
     * 商品上架
     *
     * @param id spuId
     */
    @Override
    public void put(String id) {
        Spu spu = spuMapper.selectByPrimaryKey("id");

        // 只有审核通过的商品才能上架
        String status = spu.getStatus();
        if (!status.equals("1")) {// status=1，审核通过
            throw new RuntimeException("此商品未通过审核！");
        }
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);

        // 商品日志记录   未完

    }

    /**
     * 商品批量上架
     *
     * @param ids
     * @return
     */
    @Override
    public int putMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1");//设置状态已上架

        // 构建条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        criteria.andEqualTo("status", "1");//通过审核的才能上架
        criteria.andEqualTo("isMarketable", "0");//未上架才需要上回

        // 批量更新数据库
        int count = spuMapper.updateByExampleSelective(spu, example);// 更新的记录条数，要给前端返回信息

        // 记录商品日志
        return count;
    }

    /**
     * 商品批量下架
     *
     * @param ids 将要下架的多款商品的spuId
     * @return
     */
    @Override
    public int pullMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("0");// 设置状态为下架

        // 构建条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        // 批量更新数据库记录
        int count = spuMapper.updateByExampleSelective(spu, example);//记录更新记录条数

        // 记录商品日志   示完

        return count;

    }
}
