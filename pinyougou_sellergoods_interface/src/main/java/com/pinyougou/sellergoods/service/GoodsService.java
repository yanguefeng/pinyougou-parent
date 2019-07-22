package com.pinyougou.sellergoods.service;
import java.util.List;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbGoods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);

	
	
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
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);


	/**‘
	 * 商品新增
	 * @param goods
	 */
	public void add(Goods goods);

	/**
	 * 商家审核状态修改
	 */
	public void updateStatus(Long[] ids,String status);

	/**
	 * 进行逻辑删除修改isDelete的值，代表删除
	 * @param ids
	 */
	public void uplateIsDelete(Long[] ids);

	/**
	 * 实现商品的上下架，修改isMarketable值为0（代表下架）或1（上架）
	 * @param ids
	 */
	public void uplateIsMarketable(Long[] ids,String status);


}
