package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;


import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private IdWorker idWorker;

	/**
	 * 将秒杀订单保存到缓存中，在库存为0的时候更新数据库
	 * @param seckillId
	 * @param username
	 */
	@Override
	public void submitOrder(Long seckillId, String username) {
		//从缓存中获取信息
		TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

		if (seckillGoods==null){//缓存中不存在商品信息
			throw new RuntimeException("商品不存在");
		}

		if (seckillGoods.getStockCount()<=0){//库存没有
			throw new RuntimeException("商品已被抢购完");
		}

		//减少商品的库存数量
		seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
		redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);//将改变后的数据存储到缓存中

		if (seckillGoods.getStockCount()==0){//修改库存之后库存等于0
			seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//修改数据库的信息
			redisTemplate.boundHashOps("seckillGoods").delete(seckillId);//删除缓存中数据
		}

		//保存（redis）订单
		long orderId = idWorker.nextId();
		TbSeckillOrder seckillOrder=new TbSeckillOrder();
		seckillOrder.setId(orderId);
		seckillOrder.setCreateTime(new Date());
		seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
		seckillOrder.setSeckillId(seckillId);
		seckillOrder.setSellerId(seckillGoods.getSellerId());
		seckillOrder.setUserId(username);//设置用户ID
		seckillOrder.setStatus("0");//状态
		redisTemplate.boundHashOps("seckillOrder").put(username, seckillOrder);//将秒杀订单保存到缓存中
	}


	/**
	 * 从redis中查询订单信息
	 * @param username
	 * @return
	 */
	@Override
	public TbSeckillOrder searchOrderFromRedisByUserId(String username) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(username);
	}


	/**
	 * 支付成功后保存数据
	 * @param userId
	 * @param orderId
	 * @param transactionId
	 */
	@Override
	public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
		//根据id查询订单信息
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

		if (seckillOrder==null){//订单不存在
			throw  new RuntimeException("订单不存在");
		}

		if (seckillOrder.getId().longValue()!=orderId.longValue()){//穿过俩的订单不是同一个
			throw new RuntimeException("订单不相符");
		}

		seckillOrder.setTransactionId(transactionId);//设置交易流水号
		seckillOrder.setPayTime(new Date());//设置支付时间
		seckillOrder.setStatus("1");//设置支付状态
		seckillOrderMapper.insert(seckillOrder);//将支付成功的保存到数据库
		redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除支付成功的订单

	}


	/**
	 * 支付超时在缓存中删除订单
	 * @param userId
	 * @param orderId
	 */
	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		//根据用户id查询订单信息
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

		//缓存中的订单id和传递的订单id相等删除缓存中的订单
		if (seckillOrder!=null && seckillOrder.getId().longValue()==orderId.longValue()){
			//删除订单信息
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
		}

		//1回复缓存
		//1.1，从缓存中获取秒杀的商品
		TbSeckillGoods sekillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("sekillGoods").get(seckillOrder.getSeckillId());
		if (sekillGoods!=null){//有对应的商品
			//给库存数+1
			sekillGoods.setStockCount(sekillGoods.getStockCount()+1);
			//重新存储到缓存中
			redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getUserId(),sekillGoods);
		}
	}

}
