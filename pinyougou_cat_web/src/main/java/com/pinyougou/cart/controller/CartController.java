package com.pinyougou.cart.controller;


import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 在cook中获取购物车列表信息
     * @return
     */
    public List<Cart> findCookieCartList(){
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList==null || cartList.equals("")){
            cartList="[]";
        }
        List<Cart> carts = JSON.parseArray(cartList, Cart.class);
        return carts;
    }


    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            //在cook中获取购物车信息
            List<Cart> cartList = findCookieCartList();
            //调用服务操作购物车
            List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
            String cartListStr = JSON.toJSONString(carts);
            //将新的购物车信息存储到cookie中
            CookieUtil.setCookie(request,response,"cartList",cartListStr,3600*24,"UTF-8");
            return new Result(true,"添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }
    }
}
