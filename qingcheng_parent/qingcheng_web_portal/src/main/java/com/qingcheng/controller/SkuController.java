package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Feiyue
 * Date: 2019/8/16 17:41
 * Desc:
 */
@RestController
@RequestMapping("/sku")
@CrossOrigin  // Spring注解，支持类进行跨域调用
public class SkuController {

    @Reference //dubbo注解，从注册中心获取服务
    private SkuService skuService;

    @GetMapping("/getPriceById")
    public Integer getPriceById(String skuId) {
        return skuService.findPriceById(skuId);
    }

}
