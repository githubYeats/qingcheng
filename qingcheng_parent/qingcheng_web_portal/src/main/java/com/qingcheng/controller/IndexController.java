package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Author: Feiyue
 * Date: 2019/8/9 19:20
 * Desc:
 */
@Controller
public class IndexController {


    @Reference //dubbo注解
    private AdService adService;


    @GetMapping("/index")
    public String index(Model model){
        String position = "web_index_lb";
        List<Ad> lbt = adService.findAdByPosition(position);
        model.addAttribute("lbt", lbt);
        return "index";
    }
}
