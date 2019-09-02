package com.qingcheng.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.goods.SpuService;
import com.qingcheng.service.page.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/30 15:10
 * Desc:
 */
@Service(interfaceClass = PageService.class)
public class PageServiceImpl implements PageService {

    @Reference
    private SpuService spuService;

    @Reference
    private SkuService skuService;

    @Reference
    private CategoryService categoryService;

    @Value("${pagePath}")
    private String pagePath;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 根据上架spu商品的spuId生成对应的sku详情页面
     *
     * @param spuId
     */
    @Override
    public void createPagesBySpuId(String spuId) {
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

        /*
        --------------------存储spu下各sku规格信息与详情页面映射关系-----------------------
         */
        Map<String, String> skuSpecUrlMap = new HashMap<>();
        for (Sku sku : skuList) {
            // 前置条件判断：只有sku仍然在售，才将其添加到映射关系表中
            if ("1".equals(sku.getStatus())) {
                String key = sku.getSpec();
                //对key值中的各项按自然顺序排序
                key = JSON.toJSONString(JSON.parseObject(key), SerializerFeature.MapSortField);

                String value = sku.getId() + ".html";
                skuSpecUrlMap.put(key, value);
            }
        }

        // 批量生成sku页面  TemplateEngine
        for (Sku sku : skuList) {
            /*
            --------------------------创建数据模型---------------------------------
             */
            Context context = new Context();
            Map<String, Object> dataModelMap = new HashMap<>();
            dataModelMap.put("spu", spu);
            dataModelMap.put("sku", sku);
            dataModelMap.put("categoryNameList", categoryNameList);
            dataModelMap.put("skuImages", sku.getImages().split(","));//sku图片列表
            dataModelMap.put("spuImages", spu.getImages().split(","));//spu图片列表

            //--------------------------sku的规格参数信息------------------------------
            // 参数信息（统一的信息，放在spu中）
            //com.alibaba.fastjson.JSON;
            String paraItems = spu.getParaItems();
            Map parasMap = JSON.parseObject(paraItems);//spu参数列表
            // 规格信息（每个sku独有）
            Map specMap = JSON.parseObject(sku.getSpec());//当前SKU规格
            dataModelMap.put("parasMap", parasMap);
            dataModelMap.put("specMap", specMap);

            //######################################################################################

            //--------------------------------以下是改造过程------------------------------------------------------------
            // 获取当前页面展示的sku对应的spu的规格数据
            String specItems = spu.getSpecItems();// json字符串

            // json字符串-->Map对象
            Map<String, List> specItemMap = (Map) JSON.parseObject(specItems);//【最外层Map】

            //【遍历spu的规格名称】，进行改造
            for (String key : specItemMap.keySet()) {
                // 取某一规格名称下的所有规格值
                List<String> valueList = specItemMap.get(key);

                List<Map> mapList = new ArrayList<>();// 【中间层List】

                // 【遍历某个规格名称下对应的值】，进行改造
                for (String value : valueList) {
                    Map map = new HashMap();// 【最内层Map】
                    // 设置数据
                    map.put("option", value);
                    //设置checked属性
                    if (value.equals(specMap.get(key))) {
                        //此规格组合正是当前SKU的，标记选中状态
                        map.put("checked", true);
                    } else {
                        map.put("checked", false);
                    }

                    // ------------------设置新sku信息（即新的被点击选中的sku，即将展示的新sku）-----------------------------------
                    // 获取当前sku的规格值（原先被选中的sku），转换成Map（需要修改其中的值）
                    Map<String, String> skuSpec = (Map) JSON.parseObject(sku.getSpec());

                    // 设置点击切换的规格项对应的新值（新值，就是内层循环正在遍历到的value）
                    skuSpec.put(key, value);

                    // 将新sku规格值，整体转换成json字符串，作为从上面skuSpecUrlMap中取sku页面名称时的key
                    String key4skuSpecUrlMap = JSON.toJSONString(skuSpec, SerializerFeature.MapSortField);

                    // 【最内层Map】，添加属性值skuUrl----sku的html页面名称
                    map.put("skuUrl", skuSpecUrlMap.get(key4skuSpecUrlMap));

                    // 【中间层List】，添加改造后的数据
                    mapList.add(map);
                }

                // 【最外层Map】，添加改造后的数据。
                specItemMap.put(key, mapList);
            }
            // 改造后的数据，添加到数据模型中。
            dataModelMap.put("specItemMap", specItemMap);

            //设置数据模型，添加数据
            context.setVariables(dataModelMap);

            //#######################################################################################################

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

                writer.close();//生成完页面，关闭流，否则后面在删除页面时，因为页面对象正在被流使用，就无法删除页面。
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据下架spu商品的spuId删除对应的sku详情页面
     *
     * @param spuId
     */
    @Override
    public void deletePagesBySpuId(String spuId) {
        // 获取商品对象 goods
        Goods goods = spuService.findGoodsById(spuId);

        // 获取sku列表
        List<Sku> skuList = goods.getSkuList();

        //String path = "D:/items/8638898.html";
        //pagePath=d:/items/
        for (Sku sku : skuList) {
            String skuPath = pagePath + sku.getId() + ".html";
            System.out.println(skuPath);
            File file = new File(skuPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * @param body 消息体
     */
    @Override
    public void pageMsgDeal(byte[] body) {
        String spuId = new String(body);
        Spu spu = spuService.findById(spuId);
        if ("1".equals(spu.getIsMarketable())) {//上架商品
            createPagesBySpuId(spuId);
        }
        if ("0".equals(spu.getIsMarketable())) {//下架商品
            deletePagesBySpuId(spuId);
        }
    }
}
