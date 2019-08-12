package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/12 12:06
 * Desc: 商品详情页      一个sku一个页面
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference //dubbo注解
    private SpuService spuService;

    @Value("${pagePath}")
    private String pagePath;

    @Autowired
    private TemplateEngine templateEngine;

    @Reference //dubbo注解
    private CategoryService categoryService;

    /**
     * 批量生成商品详情页面（sku页面）
     *
     * @param spuId
     */
    @GetMapping("/createPage")
    public void createPage(String spuId) {
        // 获取商品对象 goods
        Goods goods = spuService.findGoodsById(spuId);

        // 获取spu对象
        Spu spu = goods.getSpu();

        // 获取sku列表
        List<Sku> skuList = goods.getSkuList();

        // 获取商品3级分类名称
        List<String> categoryNameList = new ArrayList<>();
        categoryNameList.add(categoryService.findById(spu.getCategory1Id()).getName());//1级分类名
        categoryNameList.add(categoryService.findById(spu.getCategory2Id()).getName());//2级分类名
        categoryNameList.add(categoryService.findById(spu.getCategory3Id()).getName());//3级分类名


        // 批量生成sku页面  TemplateEngine
        //public final void process(final String template, final IContext context, final Writer writer)
        for (Sku sku : skuList) {
            /*
            --------------------------创建数据模型---------------------------------
            org.thymeleaf.context.Context;  有很多Context，不要导错包
                public void setVariable(final String name, final Object value)
                public void setVariables(final Map<String,Object> variables)
             */
            Context context = new Context();
            Map<String, Object> dataModelMap = new HashMap<>();
            dataModelMap.put("spu", spu);
            dataModelMap.put("sku", sku);
            dataModelMap.put("categoryNameList", categoryNameList);
            dataModelMap.put("skuImages", sku.getImages().split(","));//sku图片列表
            dataModelMap.put("spuImages", spu.getImages().split(","));//spu图片列表

            //sku的规格参数信息
            // 参数信息（统一的信息，放在spu中）
            //com.alibaba.fastjson.JSON;
            String paraItems = spu.getParaItems();
            Map parasMap = JSON.parseObject(paraItems);//spu参数列表
            // 规格信息（每个sku独有）
            Map specMap = JSON.parseObject(sku.getSpec());//当前SKU规格
            dataModelMap.put("parasMap", parasMap);
            dataModelMap.put("specMap", specMap);

            /*
            -----------------sku页面规格面板展示---------------------------
            某个sku: {"颜色": "黑色","选择套装":"裸机版","版本": "6GB+128GB"}
            #############################################################
            根据上面的sku，改造spu
            {
                "颜色":[
                    { "option":"红色", "checked":"false" },
                    { "option":"黑色", "checked":"true" },
                    { "option":"瑚色", "checked":"false" }
                ],
                "选择套装":[...],
                "版本":[...]
            }
             */
            String specItems = spu.getSpecItems();
            Map<String, List> specItemMap = (Map) JSON.parseObject(specItems);
            for (String key : specItemMap.keySet()) {// 遍历规格名称
                List<String> valueList = specItemMap.get(key);// 取某一规格名称下的各规格值
                List<Map> mapList = new ArrayList<>(); // 新集合
                // 遍历各规格值，进行改造
                for(String value:valueList){
                    Map map = new HashMap();
                    map.put("option",value);
                    //设置checked属性
                    if(value.equals(specMap.get(key))){//此规格组合正是当前SKU的，标记选中状态
                        map.put("checked",true);
                    }else {
                        map.put("checked",false);
                    }
                    mapList.add(map);
                }
                specItemMap.put(key,mapList); // 新集合覆盖原集合
            }
            dataModelMap.put("specItemMap", specItemMap);

            context.setVariables(dataModelMap);

            /*
            ----------------------准备文件--------------------------------
            1.准备一个目录，用于存放生成的sku页面
            2.准备sku页面
             */
            // 目录，用于存放生成的sku页面。  动态设置，通过配置文件config.properties来设置
            File dir = new File(pagePath);
            if (!dir.exists()) {//多级目录创建，确保设定的目录存在
                //目录不存在，创建
                dir.mkdirs();
            }
            // 生成的sku页面，设置命名规则   skuId值.html
            File skuPage = new File(dir, sku.getId() + ".html");

            /*
            -----------------------生成页面----------------------------------
             process(final String template, final IContext context, final Writer writer)
                template：模板页面名称     商品详情页面模板，即sku页面，此处使用的是item.html
             */
            //
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(skuPage, "UTF-8");
                templateEngine.process("item", context, writer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}
