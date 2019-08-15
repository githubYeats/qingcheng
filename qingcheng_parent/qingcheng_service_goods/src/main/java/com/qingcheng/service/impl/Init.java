package com.qingcheng.service.impl;

import com.qingcheng.service.goods.CategoryService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Feiyue
 * Date: 2019/8/15 16:44
 * Desc: 实现InitializingBean接口的类会在启动时自动调用.
 * 这里利用该Init类，在项目启动后就进行缓存预热。
 */
@Component //Spring注解
public class Init implements InitializingBean {

    @Autowired
//    @Reference
    private CategoryService categoryService;

    /**
     * 项目启动后，就将热点数据加载到Redis，进行缓存预热。
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("----缓存预热----");

        // 调用saveCategoryTree2Redis()方法
        categoryService.saveCategoryTree2Redis();
    }
}
