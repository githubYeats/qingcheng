package com.qingcheng.service.order;

import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/25 22:30
 * Desc: 购物车接口
 */
public interface CartService {

    /**
     * 用户购物车信息列表展示
     *
     * @param username
     * @return 某个用户的购物车数据
     */
    public List<Map<String, Object>> findCart(String username);


    /**
     * 用户添加商品到购物车
     *
     * @param username 当前登录用户名
     * @param skuId    商品的skuId
     * @param num      商品数量
     */
    public void addGoods2Cart(String username, String skuId, Integer num);

    /**
     * 保存商品勾选状态     更新的值存储在redis中
     *
     * @param username
     * @param skuId
     * @param checked
     * @return
     */
    public boolean updateChecked(String username, String skuId, boolean checked);

    /**
     * 删除选中的商品
     * 实现思路：直接用stream流来过滤出没选中的商品，然后覆盖redis中的购物车数据。
     * 因此，就不需要传递选中的skuId了。
     *
     * @param username
     */
    public void deleteChecked(String username);

    /**
     * 计算用户购物车的优惠金额
     *
     * @param username
     * @return
     */
    public int preferential(String username);

    /**
     * 刷新购物车商品价格
     * 在下单前，重新刷新商品价格。
     *
     * @param username 用户名
     * @return 购物车列表信息
     */
    public List<Map<String, Object>> refreshCart(String username);

}
