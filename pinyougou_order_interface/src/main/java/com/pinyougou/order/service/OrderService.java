package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

/**
 * 保存订单接口
 */
public interface OrderService {


    /**
     * 添加订单
     * @param order
     */
    public void addOrder(TbOrder order);


    /**
     * 通过用户名在redis中获取交易对象
     * @param username
     * @return
     */
    public TbPayLog searchPayLogFromRedis(String username);
}
