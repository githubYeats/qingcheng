package com.qingcheng.service.goods;

import java.io.IOException;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/19 12:56
 * Desc: 商品搜索服务接口
 */
public interface SkuSearchService {

    /**
     * 关键字搜索
     * 前端向后端传递map（因为提交的不仅仅是关键字，还有品牌、规格、分类等信息）
     * 后端给前端返回map（因为返回的不仅仅是sku列表，还有商品分类、品牌和规格列表等数据）
     *
     * @param searchMap
     * @return 条件查询返回的结果，sku列表
     */
    public Map keywordsSearch(Map<String, String> searchMap) throws IOException;
}
