package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.Address;
import com.qingcheng.service.order.CartService;
import com.qingcheng.service.user.AddressService;
import org.apache.http.HttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Feiyue
 * Date: 2019/8/25 22:41
 * Desc:
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference // dubbo注解
    private CartService cartService;

    @Reference // dubbo注解
    private AddressService addressService;

    /**
     * 从Redis中提取用户购物车列表信息
     *
     * @return 某个用户的购物车数据
     */
    @GetMapping("/findCart")
    public List<Map<String, Object>> findCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cart = cartService.findCart(username);
        return cart;
    }

    /**
     * 添加商品到购物车
     *
     * @param skuId 商品id
     * @param num   数量
     * @return
     */
    @GetMapping("/addGoods2Cart")
    public Result addGoods2Cart(String skuId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addGoods2Cart(username, skuId, num);
        return new Result();
    }

    /**
     * 实现在item.html页面上添加商品到购物车后重定向到购物车页面
     *
     * @param response
     * @param skuId
     * @param num
     * @throws Exception
     */
    @GetMapping("/buy")
    public void buy(HttpServletResponse response, String skuId, Integer num) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addGoods2Cart(username, skuId, num);
        response.sendRedirect("/cart.html");
    }

    /**
     * 保存购物车商品勾选状态
     *
     * @param skuId
     * @param checked
     */
    @GetMapping("/updateChecked")
    public Result updateChecked(String skuId, boolean checked) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateChecked(username, skuId, checked);
        return new Result();
    }

    /**
     * 删除购物车中的选中商品
     *
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteChecked(username);
        return new Result();
    }

    /**
     * 计算当前购物车优惠金额
     *
     * @return
     */
    @GetMapping("/preferential")
    public Map preferential() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int preferential = cartService.preferential(username);
        Map map = new HashMap();
        map.put("preferential", preferential);
        return map;
    }

    /**
     * 刷新购物车商品价格
     *
     * @return
     */
    @GetMapping("/refreshCart")
    public List<Map<String, Object>> refreshCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.refreshCart(username);
    }

    /**
     * 查询当前登录用户的收货人地址信息
     *
     * @return
     */
    @GetMapping("/findAddress")
    public List<Address> findAddress() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        String username = "17723947934";
        return addressService.findByUsername(username);
    }
}