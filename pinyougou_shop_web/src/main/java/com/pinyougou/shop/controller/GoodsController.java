package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取商家的用户名
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			//设置商品用户名
			TbGoods goods1 = goods.getGoods();
			goods1.setSellerId(name);
			goods.setGoods(goods1);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//获取当前登录用户的用户名
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		//获取修改前的id
		String oldSellerId = goodsService.findOne(goods.getGoods().getId()).getGoods().getSellerId();
		//获取修改后的id
		String newSellerId= goods.getGoods().getSellerId();

		//当修改前的id等于修改后的id或者修改后的id等于当前登录的用户id时我们执行修改操作，防止别人恶意修改其他用户的数据
		if (!oldSellerId.equals(newSellerId) || !newSellerId.equals(sellerId)){
			return new Result(false, "非法操作");
		}

		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//添加条件查询当前用户下的所有信息
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
	}


	/**
	 * 实现商品的上下架，修改isMarketable值为0（代表下架）或1（上架）
	 * @param ids
	 */
	@RequestMapping("/uplateIsMarketable.do")
	public Result uplateIsMarketable(Long[] ids,String status) {
		try {
			goodsService.uplateIsDelete(ids);
			return new Result(true,"上下架成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"上下架失败");
		}
	}
}
