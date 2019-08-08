package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {


    @Reference
    private SeckillOrderService seckillOrderService;


    /**
     * 秒杀商品下单
     * @param seckillId
     * @return
     */
    @RequestMapping("/submitOrder.do")
    public Result submitOrder(Long seckillId){

        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if ("anonymousUser".equals(username)){//用户未登录
            return new Result(false,"用户未登录");
        }

        try {
            seckillOrderService.submitOrder(seckillId,username);
            return new Result(false,"秒杀成功");
        }catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"秒杀失败");
        }
    }
}
