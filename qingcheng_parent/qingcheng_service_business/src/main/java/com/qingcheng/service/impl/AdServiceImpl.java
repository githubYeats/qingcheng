package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    private AdMapper adMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Ad> findAll() {
        return adMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Ad> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Ad> ads = (Page<Ad>) adMapper.selectAll();
        return new PageResult<Ad>(ads.getTotal(), ads.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Ad> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Ad> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Ad> ads = (Page<Ad>) adMapper.selectByExample(example);
        return new PageResult<Ad>(ads.getTotal(), ads.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Ad findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param ad
     */
    public void add(Ad ad) {
        adMapper.insert(ad);
        saveAd2RedisByPosition(ad.getPosition());
    }

    /**
     * 修改
     *
     * @param ad  待更新的广告对象   tb_ad中的一条记录
     */
    public void update(Ad ad) {
        adMapper.updateByPrimaryKeySelective(ad);

        /*
        页面广告信息更新的思路：广告数据有很多，这里以更新广告位置为例
            更新一个广告的位置，那么影响的可能是两个地方，比如将某个广告的位置从A处改为B处，
            此时，A位置上的广告少了一个，B位置上的广告多了一个！

            所以，更新一个广告的位置信息，要更新页面上两个地方的广告，
            并将两个位置处的广告信息缓存到Redis中！
         */
        // 获取广告对象ad原来所在的页面位置信息
        String position = adMapper.selectByPrimaryKey(ad.getId()).getPosition();

        // 广告位置发生变化，才进行更新。  ad.getPosition()是“新的位置”， position是原位置
        if (!ad.getPosition().equals(position)) {
            // 更新原位置上的广告数据
            saveAd2RedisByPosition(position);
            // 更新新位置上的广告数据
            saveAd2RedisByPosition(ad.getPosition());
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        adMapper.deleteByPrimaryKey(id);

        //更新缓存
        String position = adMapper.selectByPrimaryKey(id).getPosition();
        saveAd2RedisByPosition(position);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 广告名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 广告位置
            if (searchMap.get("position") != null && !"".equals(searchMap.get("position"))) {
                criteria.andEqualTo("position", searchMap.get("position"));
            }
            // 状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }
            // 图片地址
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // URL
            if (searchMap.get("url") != null && !"".equals(searchMap.get("url"))) {
                criteria.andLike("url", "%" + searchMap.get("url") + "%");
            }
            // 备注
            if (searchMap.get("remarks") != null && !"".equals(searchMap.get("remarks"))) {
                criteria.andLike("remarks", "%" + searchMap.get("remarks") + "%");
            }

            // ID
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }

        }
        return example;
    }

    /**
     * 根据广告位置查询允许展示的广告：从缓存中查询
     *
     * @param position
     * @return
     */
    @Override
    public List<Ad> findAdByPosition(String position) {
        System.out.println("从缓存中查询广告数据");
        return (List<Ad>) redisTemplate.boundHashOps(CacheKey.AD).get(position);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 将某个位置的广告数据加载到缓存中
     *
     * @param position
     */
    @Override
    public void saveAd2RedisByPosition(String position) {
        // 获取广告数据
        Example example = new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();

        //设置查询条件
        criteria.andEqualTo("position", position);
        //允许展示条件
        /*
        start_time <= 当前时间
        end_time >= 当前时间
        status = 1  有效
        提示：如果加上时间条件查不出来，可以将其去掉。   这是通用Mapper本身的bug。
         */
        /*criteria.andLessThanOrEqualTo("startTime",new Date());
        criteria.andGreaterThanOrEqualTo("endTime",new Date());*/
        criteria.andEqualTo("status", "1");

        //查询并返回
        List<Ad> adList = adMapper.selectByExample(example);

        // 加载到内存
        redisTemplate.boundHashOps(CacheKey.AD).put(position, adList);
    }

    /**
     * 缓存所有广告数据
     * 思路：遍历所有广告位置，加载数据到缓存
     */
    @Override
    public void saveAllAd2Redis() {
        // 获取所有广告位置数据
        List<String> adPositions = new ArrayList<>();
        // 添加位置信息----这些数据应该从数据库去查
        adPositions.add("web_index_lb");
        //......

        // 遍历位置，加载数据到缓存
        for (String adPosition : adPositions) {
            saveAd2RedisByPosition(adPosition);
        }
    }
}
