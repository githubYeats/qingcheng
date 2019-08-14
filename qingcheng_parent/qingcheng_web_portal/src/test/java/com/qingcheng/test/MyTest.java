package com.qingcheng.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/14 12:30
 * Desc:
 */
public class MyTest {

    @Test
    public void test01() {
        //json字符串
        String skuSpec = "{'颜色': '紫色', '版本': '全网通', '选择版本': '4GB+128GB'}";
        System.out.println("json字符串：" + skuSpec);

        System.out.println("########################【json字符串-->json对象】################################");
        //----------------------【json字符串-->json对象】----------------------------
        /*
        public class JSONObject extends JSON implements Map<String, Object>
         */
        JSONObject jsonObject = JSON.parseObject(skuSpec);
        System.out.println(jsonObject);//{"颜色":"紫色","版本":"全网通","选择版本":"4GB+128GB"}


        System.out.println("##########################【（json字符串-->json对象）-->Map】##############################");
        //-----------------------【json字符串-->json对象-->Map】---------------------
        Map<String, String> map = (Map) JSON.parseObject(skuSpec);
        for (int i = 0; i < 10; i++) {
            for (String s : map.keySet()) {
                System.out.println(s + ":" + map.get(s));
            }
            System.out.println("---------------------------");
        }

       /* for (int i = 0; i < 10; i++) {
            System.out.println(map);//{"颜色":"紫色","版本":"全网通","选择版本":"4GB+128GB"}
        }*/

        System.out.println("##########################【Map对象-->json字符串】##############################");
        String jsonString = JSON.toJSONString(map);
        for (int i = 0; i < 10; i++) {
            System.out.println(jsonString);
        }

        System.out.println("##########################【json对象-->json字符串】##############################");
        //-----------------------【json对象-->json字符串】----------------------------
        /*
        public static String toJSONString(Object object)
        public static String toJSONString(Object object, SerializerFeature... features)
            MapSortField
         */
        String jsonString1 = JSON.toJSONString(jsonObject);
        System.out.println("===========================jsonString===============================");
        for (int i = 0; i < 10; i++) {
            System.out.println(jsonString1);//{"颜色":"紫色","版本":"全网通","选择版本":"4GB+128GB"}
        }
        System.out.println("============================jsonStringSorted==============================");
        String jsonStringSorted = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField);
        for (int i = 0; i < 10; i++) {
            System.out.println(jsonStringSorted);//{"版本":"全网通","选择版本":"4GB+128GB","颜色":"紫色"}
        }
    }

    @Test
    public void test02() {
        //json字符串
        String key ="{'颜色': '梵高星空典藏版', '版本': '8GB+128GB'}";//   数据库中存储的字符串值
        //对key值中的各项按自然顺序排序
        key = JSON.toJSONString(JSON.parseObject(key), SerializerFeature.MapSortField);
        System.out.println(key);//{"版本":"8GB+128GB","颜色":"梵高星空典藏版"}
    }


}
