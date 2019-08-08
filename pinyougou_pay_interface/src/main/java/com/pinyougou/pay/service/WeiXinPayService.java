package com.pinyougou.pay.service;


import java.util.Map;

/**
 * 支付接口
 */
public interface WeiXinPayService {


    /**
     * 创建支付方式
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);


    /**
     * 查询订单支付信息
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);


    /**
     * 关闭微信支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);
}
