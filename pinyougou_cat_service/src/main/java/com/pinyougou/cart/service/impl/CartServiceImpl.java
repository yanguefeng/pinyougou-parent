package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {


    @Autowired
    private TbItemMapper tbItemMapper;

    /**
     * 购物车列表信息的添加
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.通过itemid查询tbitem信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        if (tbItem==null){//判断是sku信息是否存在
            throw new RuntimeException("不存在的商品列表信息");
        }
        if (tbItem.getStatus().equals("1")){//判断列表信息的状态
            throw new RuntimeException("商品的列表信息异常");
        }
        //2.获取用户id
        String sellerId = tbItem.getSellerId();
        //3.获取购物车对象
        Cart cart = searchCartBysellerId(cartList, sellerId);
        if (cart==null){//4.如果购物车信息不存在
            //4.1创建购物车对象
            cart=new Cart();
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(tbItem, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2将购物信息添加到购物车对象中
            cartList.add(cart);
        }else {//5.购物车信息存在
            //判断该商品在购物车中是否存在
            TbOrderItem orderItem = searchOrderItemByItemid(cart.getOrderItemList(), itemId);
            if (orderItem==null){//5.1商品不存在，创建新的购物车信息
                TbOrderItem tbOrderItem = createOrderItem(tbItem, num);
                //添加到购物车中
                cart.getOrderItemList().add(tbOrderItem);
            }else {
                //5.2存在，在原来的数据上修改数量和价格
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                //当数量信息小于等于0时，这个列表不需要存在
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }

                //当购物车信息长度为0时移除购物车
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return null;
    }

    /**
     * 通过用户id在购物车列表中查找对应的列表信息
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBysellerId(List<Cart> cartList, String sellerId){
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }


    /**
     * 通过sku的id查询购物车列表信息
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemid(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().longValue()==itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }


    /**
     * 创建购物车信息
     * @param item
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(orderItem.getNum()+num);
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        BigDecimal totalFee = new BigDecimal(orderItem.getNum() * orderItem.getPrice().longValue());
        orderItem.setTotalFee(totalFee);
        return orderItem;
    }
}
