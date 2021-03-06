package com.qingcheng.service.business;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.business.Ad;

import java.util.*;

/**
 * ad业务逻辑层
 */
public interface AdService {


    public List<Ad> findAll();


    public PageResult<Ad> findPage(int page, int size);


    public List<Ad> findList(Map<String,Object> searchMap);


    public PageResult<Ad> findPage(Map<String,Object> searchMap,int page, int size);


    public Ad findById(Integer id);

    public void add(Ad ad);


    public void update(Ad ad);


    public void delete(Integer id);

    /**
     * 根据广告位置查询允许展示的广告
     * @param position
     * @return
     */
    List<Ad> findAdByPosition(String position);

    /**
     * 将某个位置的广告数据加载到缓存中
     * @param position
     */
    public void saveAd2RedisByPosition(String position);

    /**
     * 加载全部广告信息到缓存中
     */
    public void saveAllAd2Redis();
}
