package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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

        /*
        --------------------存储spu下各sku规格信息与详情页面映射关系-----------------------
        选择新规格时，需要切换sku页面，需要使用此映射关系
        skuSpecUrlMap格式如下：
             key                                                             value
            {'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}                8638898.html
         */
        Map<String, String> skuSpecUrlMap = new HashMap<>();
        for (Sku sku : skuList) {
            // 前置条件判断：只有sku仍然在售，才将其添加到映射关系表中
            if ("1".equals(sku.getStatus())) {// tb_sku表status字段  1正常  2下架 3删除
                String key = sku.getSpec(); //json字符串： {'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}   数据库中存储的字符串值
                //对key值中的各项按自然顺序排序
                /*
                排序的key值：{"版本":"8GB+128GB","颜色":"梵高星空典藏版"}       json字符串
                 */
                key = JSON.toJSONString(JSON.parseObject(key), SerializerFeature.MapSortField);

                String value = sku.getId() + ".html";//格式：8638898.html 自己设置静态渲染生成的sku页面的名称，统一以“spuId值.html”命名
                skuSpecUrlMap.put(key, value);
            }
        }

        // 批量生成sku页面  TemplateEngine
        /*
        public final void process(final String template, final IContext context, final Writer writer)
        4个步骤：
            1）准备模板页面                          template
            2）准备数据                              context
            3）准备写出的文件                        writer
            4）执行process()方法，生成页面
         */
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

            /*
            -------------------------------sku页面规格面板展示（非常复杂）---------------------------
            某个sku的规格选项: {'颜色': '黑色','选择套装':'裸机版','版本': '6GB+128GB'}   json字符串，数据库中存储的原值
            某个spu的规格选项：{"颜色":["粉色","黑色","蓝色"],"版本":["6GB+128GB","4GB+64GB","6GB+64GB"],"选择版本":["官方标配","保险套装"]}
            --------------------------
            根据上面的sku，改造spu
            （理解了下面的"双层Map夹List"，就理解了此处spu规格改造的过程！！！<以一个spu下具体的sku的规格为例>）
                【双层Map夹List】
                    最外层Map: Map<String, List> specItemMap = (Map) JSON.parseObject(spu.getSpecItems());  某个sku的整个规格面板数据
                        中间层List: List<Map> mapList = new ArrayList<>();  某一个规格项
                            最内层Map：Map map = new HashMap();  一个值改为一条记录
                    各层加泛型
                        最外层Map：Map<String, String>  一个值改为一条记录
                        中间层List: List<Map<String, String>> mapList  某一个规格项下多条记录(一条记录一个map)
                        最外层Map: Map<String, List<Map<String, String>>> specItemMap  某个spu的整个规格数据
            {
                "颜色":[
                    { "option":"红色", "checked":"false" }, // Map map = new HashMap();  一个值改为一条记录
                    { "option":"黑色", "checked":"true" },
                    { "option":"瑚色", "checked":"false" }
                ],
                "选择套装":[...],            // List<Map> mapList = new ArrayList<>();  某一个规格项
                "版本":[...]
            }        // Map<String, List> specItemMap = (Map) JSON.parseObject(spu.getSpecItems());  整个规格面板数据
             */

            //--------------------------------以下是改造过程------------------------------------------------------------
            // 获取当前页面展示的sku对应的spu的规格数据
            String specItems = spu.getSpecItems();// json字符串

            // json字符串-->Map对象
            /*
            json对象强转成Map（JSONObject-->Map<String,Object>）
                能转，是因为：public class JSONObject extends JSON implements Map<String, Object>    子类-->父类（父接口）
                要转，是因为：要在java代码对该json对象中的数据进行修改！
                    JSONObject，就没法直接改数据吗？
                        查看JSONObject类与JSON类的源码，可知，它们确实没有提供直接修改其中值的方法。
                        提供了大量取值与类型转换的方法，修改其中的值，需要转换其类型到Map<String,Object>。
             */
            Map<String, List> specItemMap = (Map) JSON.parseObject(specItems);//【最外层Map】

            //【遍历spu的规格名称】，进行改造
            /*
            {"颜色":["粉色","黑色","蓝色"],"版本":["6GB+128GB","4GB+64GB","6GB+64GB"],"选择版本":["官方标配","保险套装"]}
             */
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
                    /*
                    设置思路：
                        新sku是在原sku基础上，点击新的规格值（一次只能点击一个规格项<颜色/版本/尺寸等>中的一个新的值）得到的。

                        skuSpecUrlMap格式如下：
                             key(sku规格值，tb_sku表中的spec字段值)               value（生成的sku静态详情页面名称）
                            {'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}                8638898.html

                        想要获得新sku对应的地址值value，就得从skuSpecUrlMap中去取。

                        那么去Map中取值的key是什么？-->新sku的规格值

                        新sku的规格值是什么？怎么得来？？
                            假设原sku（当前页面正在展示的sku，也叫当前sku）的规格值是：{'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}

                            选择一个新sku，就是把“颜色”或“版本”换一个值，而且点击事件，一次只能换一个值，不管规格项有多少个。

                            假设切换版本选项中的值为“6G+64G”，那么新sku就得到了，其规格值为{'颜色': '梵高星空典藏版', '版本': '6GB+64GB'}

                            这个新规格值，是怎么得来的呢？
                                首先获取到原sku的规格值
                                当前双重循环中，外层循环设置sku的规格名称，内层循环设置规格名称下对应的规格值
                                而现在就是要在原sku的基础上修改某个规格项的规格值。
                    */

                    /*
                    {'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}-->{'颜色': '梵高星空典藏版', '版本': '6GB+64GB'}
                     */
                    // 获取当前sku的规格值（原先被选中的sku），转换成Map（需要修改其中的值）
                    Map<String, String> skuSpec = (Map) JSON.parseObject(sku.getSpec());

                    // 设置点击切换的规格项对应的新值（新值，就是内层循环正在遍历到的value）
                    skuSpec.put(key, value);//Map中一个key下，只能有一个value值。  往相同的key下设置新value，就是更新这条记录！

                    // 将新sku规格值，整体转换成json字符串，作为从上面skuSpecUrlMap中取sku页面名称时的key
                    String key4skuSpecUrlMap = JSON.toJSONString(skuSpec, SerializerFeature.MapSortField);

                    // 【最内层Map】，添加属性值skuUrl----sku的html页面名称
                    /*
                    要添加，是因为：需要将其页面地址传递给前端页面，前端页面需要用这个页面名称值去做页面跳转！
                     */
                    map.put("skuUrl", skuSpecUrlMap.get(key4skuSpecUrlMap));

                    // 【中间层List】，添加改造后的数据
                    mapList.add(map);
                }

                // 【最外层Map】，添加改造后的数据。----完成spu规格数据（specItems）的改造过程。
                /*
                 String specItems = spu.getSpecItems()
                 */
                specItemMap.put(key, mapList);
            }
            // 改造后的数据，添加到数据模型中。         数据模型，还是个Map，要疯了，快4层Map了（3层Map+1层List）
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}
