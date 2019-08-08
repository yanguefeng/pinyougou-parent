package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 购物车的列表信息的添加
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);


    /**
     * 在redis中插叙数据
     * @param username
     * @return
     */
    public List<Cart> findCartListToRedis(String username);


    /**
     * 购物车存储到缓存中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);


    /**
     * 合并缓存中和cookie中的购物车信息
     * @param cartListA
     * @param cartListB
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartListA,List<Cart> cartListB);
}
