package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.order.PreferentialService;
import com.qingcheng.util.CacheKey;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: Feiyue
 * Date: 2019/8/25 22:34
 * Desc:
 */
@Service //dubbo注解，注册服务
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Reference // dubbo注解，从注册中心获取服务。  service_order与service_goods是两个模块
    private SkuService skuService;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private PreferentialService preferentialService;

    /**
     * 用户购物车信息列表展示
     *
     * @param username
     * @return 某个用户的购物车数据
     */
    @Override
    public List<Map<String, Object>> findCart(String username) {
        /*
        用户购物车中的每一个订单项，封装成一个Map  叫cartItem
            key         value
            orderItem   OrderItem对象
            checked     false | true
         */
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        return cartItems;
    }


    /**
     * 用户添加商品到购物车
     *
     * @param username 当前登录用户名
     * @param skuId    商品的skuId
     * @param num      商品数量，正数时，添加；负数时，减少商品。
     */
    @Override
    public void addGoods2Cart(String username, String skuId, Integer num) {
        /*
        实现思路：
            判断所添加的商品是否已在购物车，若在，则直接增加数量；若不在，添加到购物车。
         */

        boolean flag = false;// 信号灯，标记所添加商品是否已在购物车，默认不在。
        // 获取用户购物车
        List<Map<String, Object>> cart = findCart(username);
        // 遍历判断
        for (Map<String, Object> cartItem : cart) {
            OrderItem orderItem = (OrderItem) cartItem.get("orderItem");// 购物车现有订单项
            if (orderItem.getSkuId().equals(skuId)) {//已在购物车，增加数量
                // 前置条件判断
                if (orderItem.getNum() <= 0) {// 购物车中现有订单项对应的商品数量<=0
                    cart.remove(cartItem);
                    break;
                }

                // 计算单个商品重量    单位：克
                int perWeight = orderItem.getWeight() / orderItem.getNum();//一定可以整除

                // 增加数量
                orderItem.setNum(orderItem.getNum() + num);
                // 总金额变化    money字段
                orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());
                // 重量变化     weight字段
                orderItem.setWeight(perWeight * orderItem.getNum());

                // 添加完商品，再判断商品数量（因为num可能是正，也可能是负）
                if (orderItem.getNum() <= 0) {
                    cart.remove(cartItem);
                }

                // 找到相同商品，修改标记，中止循环
                flag = true;
                break;
            }
        }
        // 没在购物车，添加商品到购物车
        if (!flag) {
            // orderItem对象的多数信息来自于sku
            Sku sku = skuService.findById(skuId);
            // sku的前置条件判断
            if (sku == null) {
                throw new RuntimeException("商品不存在");
            }
            if (!"1".equals(sku.getStatus())) {// 1正常；2下架；3删除
                throw new RuntimeException("商品状态不合法");
            }
            if (num <= 0) {  //数量不能为0或负数
                throw new RuntimeException("商品数量不合法");
            }

            // 创建并设置OrderItem对象
            OrderItem orderItem = new OrderItem();
            // id    分布式id
            orderItem.setId(String.valueOf(idWorker.nextId()));
            // name 商品名称
            orderItem.setName(sku.getName());
            // price    单价      后面要改用缓存进行存储优化
            orderItem.setPrice(sku.getPrice());
            // num  数量
            orderItem.setNum(num);
            // money    总金额（分）
            orderItem.setMoney(sku.getPrice() * num);
            // image    图片地址
            orderItem.setImage(sku.getImage());
            // weight   重量
            if (sku.getWeight() <= 0) {
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight() * num);
            // sku_id
            orderItem.setSkuId(skuId);
            // spu_id
            orderItem.setSpuId(sku.getSpuId());

            // 商品分类 3级      category_id1    category_id2    category_id3
            /*orderItem.setCategoryId3(sku.getCategoryId());
            Category category3 = categoryService.findById(sku.getCategoryId());
            orderItem.setCategoryId2(category3.getParentId());
            Category category2 = categoryService.findById(category3.getParentId());
            orderItem.setCategoryId1(category2.getParentId());*/
            //----使用缓存进行优化----
            // categoryId1
            Integer categoryId3 = sku.getCategoryId();
            if (categoryId3 == null || categoryId3 == 0) {
                throw new RuntimeException("商品不存在！");
            }
            orderItem.setCategoryId3(categoryId3);
            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(categoryId3);
            if (category3 == null) {
                category3 = categoryService.findById(sku.getCategoryId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(categoryId3, category3);
            }
            // categoryId2
            Integer categoryId2 = category3.getParentId();
            if (categoryId2 == null || categoryId2 == 0) {
                throw new RuntimeException("商品不存在！");
            }
            orderItem.setCategoryId2(categoryId2);
            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(categoryId2);
            if (category2 == null) {
                category2 = categoryService.findById(sku.getCategoryId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(categoryId2, category2);
            }
            // categoryId1
            Integer categoryId1 = category2.getParentId();
            if (categoryId1 == null || categoryId1 == 0) {
                throw new RuntimeException("商品不存在！");
            }
            orderItem.setCategoryId1(categoryId1);
            Category category1 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(categoryId1);
            if (category1 == null) {
                category1 = categoryService.findById(sku.getCategoryId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(categoryId1, category1);
            }

            Map<String, Object> map = new HashMap<>();
            map.put("orderItem", orderItem);
            map.put("checked", true);

            cart.add(map);
        }
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cart);
    }

    /**
     * 保存商品勾选状态     更新的值存储在redis中
     *
     * @param username
     * @param skuId
     * @param checked
     */
    @Override
    public boolean updateChecked(String username, String skuId, boolean checked) {
        // 实现思路：遍历购物车，如果找到当前勾选的商品，就更新其状态

        List<Map<String, Object>> cart = findCart(username);
        boolean flag = false;// 标记，是否执行成功
        for (Map<String, Object> map : cart) {
            OrderItem orderItem = (OrderItem) map.get("orderItem");
            if (orderItem.getSkuId().equals(skuId)) {//找到当前勾选商品
                map.put("checked", checked); // 更新选中状态
                flag = true; // 设置标记，退出循环
                break;
            }
        }
        if (flag) {//执行成功，更新redis
            redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cart);
        }
        return flag;
    }

    /**
     * 删除选中的商品
     * 实现思路：直接用stream流来过滤出没选中的商品，然后覆盖redis中的购物车数据。
     * 因此，就不需要传递选中的skuId了。
     *
     * @param username
     */
    @Override
    public void deleteChecked(String username) {
        // 用stream流来过滤出没选中的商品
        List<Map<String, Object>> newCart = findCart(username).stream().
                filter(cart -> !(boolean) cart.get("checked")).collect(Collectors.toList());
        // 覆盖redis中的购物车数据
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, newCart);
    }

    /**
     * 计算用户购物车的优惠金额
     *
     * @param username
     * @return
     */
    @Override
    public int preferential(String username) {
        //获取选中的购物车  List<OrderItem>  List<Map>
        List<OrderItem> orderItemList = findCart(username).stream()
                .filter(cart -> (boolean) cart.get("checked") == true)
                .map(cart -> (OrderItem) cart.get("orderItem"))
                .collect(Collectors.toList());

        //按分类聚合统计每个分类的金额    group by
        Map<Integer, IntSummaryStatistics> cartMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getCategoryId3, Collectors.summarizingInt(OrderItem::getMoney)));
        //循环结果，统计每个分类的优惠金额，并累加

        int allPreMoney = 0;//累计优惠金额
        for (Integer categoryId : cartMap.keySet()) {
            // 获取品类的消费金额
            int money = (int) cartMap.get(categoryId).getSum();
            int preMoney = preferentialService.findPreMoneyByCategoryId(categoryId, money); //获取优惠金额
            System.out.println("分类：" + categoryId + "  消费金额：" + money + " 优惠金额：" + preMoney);

            allPreMoney += preMoney;
        }
        return allPreMoney;
    }

    /**
     * 刷新购物车商品价格
     * 在下单前，重新刷新商品价格
     *
     * @param username 用户名
     * @return 购物车列表信息
     */
    @Override
    public List<Map<String, Object>> refreshCart(String username) {
        List<Map<String, Object>> cart = findCart(username);
         /*
        用户购物车中的每一个订单项，封装成一个Map  叫cartItem
            key         value
            orderItem   OrderItem对象
            checked     false | true
         */
        // 从数据库查询最新价格并更新购物车
        for (Map<String, Object> map : cart) {
            OrderItem orderItem = (OrderItem) map.get("orderItem");
            Integer newPrice = skuService.findById(orderItem.getSkuId()).getPrice();
            if (null != newPrice) {
                orderItem.setPrice(newPrice);//单价
                orderItem.setMoney(newPrice * orderItem.getNum());//总价
            }
        }
        // 更新缓存
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cart);
        // 返回
        return cart;
    }
}
