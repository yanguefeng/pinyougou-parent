package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 订单操作类
 */
@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;


    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 添加订单
     * @param order
     */
    @Override
    public void addOrder(TbOrder order) {

        //从缓存中获取数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        List<String> orderList=new ArrayList<>();
        double pay_total_fee=0;

        //遍历集合
        for (Cart cart : cartList) {

            //通过工具类创建id
            Long orderId=idWorker.nextId();
            //创建新的订单对象
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(orderId);//设置id
            tbOrder.setUserId(order.getUserId());//设置用户名
            tbOrder.setPaymentType(order.getPaymentType());//设置支付类型
            tbOrder.setStatus("1");//设置支付状态
            tbOrder.setCreateTime(new Date());//订单创建日期
            tbOrder.setUpdateTime(new Date());//订单修改日期
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//设置地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//设置电话号码
            tbOrder.setReceiver(order.getReceiver());//设置收货人
            tbOrder.setSourceType(order.getSourceType());//设置订单来源
            tbOrder.setSellerId(order.getSellerId());//设置用户名

            double money=0;

            //遍历商品信息
            for (TbOrderItem orderItem:cart.getOrderItemList()) {
                orderItem.setOrderId(orderId);//设置订单id
                orderItem.setId(idWorker.nextId());//设置id
                orderItem.setSellerId(cart.getSellerId());//设置用户名
                //金额累加
                money += orderItem.getTotalFee().doubleValue();
                //添加到数据库
                orderItemMapper.insert(orderItem);
            }
            orderList.add(orderId+"");
            pay_total_fee +=money;
            tbOrder.setPayment(new BigDecimal(money));
            //添加到数据库中
            orderMapper.insert(tbOrder);
        }
        if("1".equals(order.getPaymentType())){//当支付为微信支付是添加支付信息
			TbPayLog payLog=new TbPayLog();
			String outTradeNo=  idWorker.nextId()+"";//支付订单号
			payLog.setOutTradeNo(outTradeNo);//支付订单号
			payLog.setCreateTime(new Date());//创建时间
			//订单号列表，逗号分隔
			String ids=orderList.toString().replace("[", "").replace("]", "").replace(" ", "");
			payLog.setOrderList(ids);//订单号列表，逗号分隔
			payLog.setPayType("1");//支付类型
			payLog.setTotalFee( (long)(pay_total_fee*100 ) );//总金额(分)
			payLog.setTradeState("0");//支付状态
			payLog.setUserId(order.getUserId());//用户ID
			payLogMapper.insert(payLog);//插入到支付日志表
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }
        //添加完成后删除缓存中的数据
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    /**
     * 通过用户名在redis中获取支付记录对象
     * @param username
     * @return
     */
    public TbPayLog searchPayLogFromRedis(String username){
        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(username);
        return payLog;
    }
}
