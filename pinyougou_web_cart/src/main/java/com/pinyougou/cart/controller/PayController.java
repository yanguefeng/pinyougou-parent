package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {


    @Reference(timeout = 5000)
    private WeiXinPayService weiXinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 二维码生成
     *
     * @return
     */
    @RequestMapping("/createNative.do")
    public Map createNative() {
        //获取当前登录的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //调用方法获取交易对象
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);
        IdWorker idworker = new IdWorker();
        String outTradeNo = payLog.getOutTradeNo();
        Long totalFee = payLog.getTotalFee();
        return weiXinPayService.createNative(outTradeNo,totalFee+"");
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int x=0;
        while (true) {
            //调用查询接口
            Map<String, String> map = weiXinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//出错
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//如果成功
                result = new Result(true, "支付成功");
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //为了不让循环无休止地运行，我们定义一个循环变量，如果这个变量超过了这个值则退出循环，设置时间为5分钟
            x++;
            if(x>=100){
                result=new  Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }
}
