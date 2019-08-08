package com.pinyougou.seckill.service;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbSeckillOrder;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);


	/**
	 * 保存秒杀信息
	 * @param seckillId
	 * @param username
	 */
	public void submitOrder(Long seckillId,String username);

	/**
	 * 从redis中查询订单信息
	 * @param username
	 * @return
	 */
	public TbSeckillOrder searchOrderFromRedisByUserId(String username);


	/**
	 * 支付成功后保存数据
	 * @param userId
	 * @param orderId
	 * @param transactionId
	 */
	public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);


	/**
	 * 支付超时从缓存中删除订单
	 * @param userId
	 * @param orderId
	 */
	public void deleteOrderFromRedis(String userId,Long orderId);
}
