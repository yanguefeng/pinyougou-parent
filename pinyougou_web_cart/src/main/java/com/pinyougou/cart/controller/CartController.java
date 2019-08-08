package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {


    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 在cook中获取购物车列表信息
     * @return
     */
    @RequestMapping("findCookieCartList.do")
    public List<Cart> findCookieCartList(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("用户名为"+username);

        List castListCookie=null;
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr==null ||cartListStr.equals("")){
            castListCookie=new ArrayList<>();
        }else{
            castListCookie=JSON.parseArray(cartListStr,Cart.class);

        }

        if (username.equals("anonymousUser")){//判断用户是否登录，未登录从cookie中获取购物车，返回列表
            System.out.println("未登录cookie中取数据");
            return castListCookie;

        }else{//登录从缓存中获取
            List<Cart> cartListRedis = cartService.findCartListToRedis(username);
            System.out.println(cartListRedis);
            if (castListCookie.size()>0){//合并后返回
                cartListRedis = cartService.mergeCartList(cartListRedis, castListCookie);
                System.out.println("缓存中取数据");
                //将新的数据存储到缓存中
                cartService.saveCartListToRedis(username,cartListRedis);
                CookieUtil.deleteCookie(request,response,"cartList");//合并之后删除cookie中的购物车信息
            }

            return cartListRedis;
        }

    }


    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("addGoodsToCartList.do")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
        /*response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");*/
        try {
            //在cook中获取购物车信息
            List<Cart> cartList = findCookieCartList();
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username.equals("anonymousUser")){//用户未登录，购物车存储带cookie中
                //调用服务操作购物车
                List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
                String cartListStr = JSON.toJSONString(carts);
                //将新的购物车信息存储到cookie中
                CookieUtil.setCookie(request,response,"cartList",cartListStr,3600*24,"UTF-8");

            }else{//登录存储到缓存中
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }
    }
}
