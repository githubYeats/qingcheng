package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.goods.SkuSearchService;
import com.qingcheng.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Author: Feiyue
 * Date: 2019/8/19 14:10
 * Desc:
 */
@Controller //spring注解
public class SearchController {
    /*
    取名叫SearchController，而不是取SkuSearchController，
    是因为这是针对所有"查询"的controller，而不是只针对sku的搜索。

    类上的注解为什么不写成如下方式？？？？
        @RestController             加上它，表示类中所有方法都是返回json数据。而此类中，要做页面跳转。
        @RequestMapping("/search")  此类不会定义很多的方法，通常也就是一个方法，请求路径映射直接写方法上就可以了。
     */

    @Reference //dubbo注解，从注册中心获取服务
    private SkuSearchService skuSearchService;

    /**
     * 商品搜索 + 过滤 + 分页查询
     * 前端向后端传递map（因为提交的不仅仅是关键字，还有品牌、规格、分类等信息）
     *
     * @param searchMap 请求参数
     * @param model     Model对象，用于向前端页面传递数据
     * @return
     */
    @GetMapping("/search")
    //@ResponseBody //网页请求，返回数据，添加此注解，做测试用，查看能够成功返回数据
    public String search(Model model, @RequestParam Map<String, String> searchMap) throws Exception {
        // 字符集处理：get方式传参的乱码问题
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);

        //-----------请求参数处理--------------
        // 分页查询中，传递的当前页码
        String pageNo = searchMap.get("pageNo");//前端传递数据都是以字符串形式传递的。
        if (null == pageNo || "".equals(pageNo)) {//没传递页码或传递页码为空
            searchMap.put("pageNo", "1");
        }

        //-----------搜索结果排序----------------
        // 接受请求参数并做默认处理
        if (null == searchMap.get("sortField")) {
            searchMap.put("sortField", "");//排序字段
        }
        if (null == searchMap.get("order")) {
            searchMap.put("order", "DESC");// 排序规则： ASC 升序； DESC 降序
        }

        // 调用service层，进行查询
        Map resultMap = skuSearchService.keywordsSearch(searchMap);
        // 查询结果添加到model对象中（前端页面就可以取用到此数据了）
        model.addAttribute("resultMap", resultMap);

        // 商品分类过滤中的url处理，点击分类，展示其下的商品数据
        StringBuffer url = new StringBuffer("/search.do?");
        Set<String> keySet = searchMap.keySet();
        for (String key : keySet) {
            url.append("&" + key + "=" + searchMap.get(key));
        }
        model.addAttribute("url", url);

        // 查询条件来自前端，又反传给前端，为了取消“过滤”之用
        model.addAttribute("searchMap", searchMap);

        // 将String类型的pageNo转换成long类型，并返给前端页面
        model.addAttribute("pageNo", Long.parseLong(searchMap.get("pageNo")));

        // 返回结果，并跳转页面       数据通过Model对象传递给前端了
        return "search";//页面跳转，跳转到search.html页面
    }

    //##############################################################################

    //----------测试上面的search()方法之用----------------------
    /*@GetMapping("/search1")
    @ResponseBody //网页请求，返回数据，添加此注解，做测试用，查看是否能够成功返回数据
    public Map search1(Model model, @RequestParam Map<String, String> searchMap) throws Exception {
        // 字符集处理：get方式传参的乱码问题
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);

        //-----------请求参数处理--------------
        // 分页查询中，传递的当前页码
        String pageNo = searchMap.get("pageNo");//前端传递数据都是以字符串形式传递的。
        if (null == pageNo || "".equals(pageNo)) {//没传递页码或传递页码为空
            searchMap.put("pageNo","1");
        }

        //-----------搜索结果排序----------------
        // 接受请求参数并做默认处理
        if (null == searchMap.get("sortField")) {
            searchMap.put("sortField", "");//排序字段
        }
        if (null == searchMap.get("order")) {
            searchMap.put("order", "DESC");// 排序规则： ASC 升序； DESC 降序
        }

        // 调用service层，进行查询
        Map resultMap = skuSearchService.keywordsSearch(searchMap);
        // 查询结果添加到model对象中（前端页面就可以取用到此数据了）
        model.addAttribute("resultMap", resultMap);

        // 商品分类过滤中的url处理，点击分类，展示其下的商品数据
        StringBuffer url = new StringBuffer("/search.do?");
        Set<String> keySet = searchMap.keySet();
        for (String key : keySet) {
            url.append("&" + key + "=" + searchMap.get(key));
        }
        model.addAttribute("url", url);

        // 查询条件来自前端，又反传给前端，为了取消“过滤”之用
        model.addAttribute("searchMap", searchMap);

        // 将String类型的pageNo转换成long类型，并返给前端页面
        model.addAttribute("pageNo", Long.parseLong(searchMap.get("pageNo")));
        return resultMap;
    }*/
}
