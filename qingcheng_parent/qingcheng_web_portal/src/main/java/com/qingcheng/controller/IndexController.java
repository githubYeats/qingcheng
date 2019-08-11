package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/9 19:20
 * Desc:
 */
@Controller
public class IndexController {

    @Reference //dubbo注解，从注册中心获取服务
    private AdService adService;

    @Reference //dubbo注解，从注册中心获取服务
    private CategoryService categoryService;


    @GetMapping("/index")
    public String index(Model model){
        /*
        ---------首页轮播图---------------------
         */
        String position = "web_index_lb";
        List<Ad> lbt = adService.findAdByPosition(position);
        model.addAttribute("lbt", lbt);

        /*
        ---------首页商品分类------------------
         */
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryTree",categoryTree);

        return "index";
    }
}
