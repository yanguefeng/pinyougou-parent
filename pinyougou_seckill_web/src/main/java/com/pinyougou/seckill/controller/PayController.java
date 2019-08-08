package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {


    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeiXinPayService weiXinPayService;

    /**
     * 获取生成二维码的数据
     * @return
     */
    @RequestMapping("/createNative.do")
    public Map createNative(){
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //在redis中查询订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);

        if (seckillOrder==null){
            return  new HashMap();
        }else{
            //获取金额
            long fen = (long) (seckillOrder.getMoney().doubleValue()*100);
            //调用方法返回生成二维码需要的数据
            return  weiXinPayService.createNative(seckillOrder.getId()+"",fen+"");
        }
    }



    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        //获取登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置计时器
        int x =0;
        Result result=null;

        //无限循环查询
        while (true){

            //调用接口查询
            Map map = weiXinPayService.queryPayStatus(out_trade_no);
            if (map==null){//出错
                result=new Result(false,"支付出错");
                break;
            }

            if (map.get("trade_state").equals("SUCCESS")){//支付成功

                //把支付成功的数据存储到数据库中
                seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                result=new Result(true,"支付成功");

            }

            try {
                Thread.sleep(3000);//每三秒执行一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x++;
           if (x>100){//设置超时时间为为五分种
               result=new Result(false,"二维码超时");

               Map<String,String> payresult = weiXinPayService.closePay(out_trade_no);
               if( !"SUCCESS".equals(payresult.get("result_code")) ){//如果返回结果是正常关闭
                   if("ORDERPAID".equals(payresult.get("err_code"))){
                       result=new Result(true, "支付成功");
                       //支付成功保存到数据库中
                       seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no), (String) map.get("transaction_id"));
                   }
               }
               if(result.getSuccess()==false){
                   System.out.println("超时，取消订单");
                   //2.调用删除
                   seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(out_trade_no));//超时调用方法删除订单，库存回加
               }
               break;
           }
        }


        return result;
    }



}
