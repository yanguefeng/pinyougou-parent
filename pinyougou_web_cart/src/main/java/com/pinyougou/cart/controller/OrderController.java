package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {


    @Reference
    private OrderService orderService;

    /**
     * 添加订单
     * @param order
     * @return
     */
    @RequestMapping("/addOrder.do")
    public Result addOrder(@RequestBody TbOrder order){
        //获取用户名
        String usernmae = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置用户名和订单来源
        order.setUserId(usernmae);
        order.setSourceType("2");
        try {
            orderService.addOrder(order);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }
}
