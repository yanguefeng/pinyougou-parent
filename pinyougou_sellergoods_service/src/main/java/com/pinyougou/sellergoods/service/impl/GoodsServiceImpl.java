package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 * 添加商品基本信息
	 * @param goods
	 */
	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);
	}

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	@Autowired
	private TbItemMapper itemMapper;

	/**
	 * 商品信息的添加和sku的添加
	 * @param goods
	 */
	@Override
	public void add(Goods goods) {
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");//设置为未通过状态
		goodsMapper.insert(tbGoods);
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		goodsDescMapper.insert(goodsDesc);
		//判断规格是否启动
		if ("1".equals(tbGoods.getIsEnableSpec())){
			//获取sku列表信息
			List<TbItem> itemList = goods.getItemList();
			//遍历
			for (TbItem tbItem : itemList) {
				//设置item表中的title；格式为new2 - 阿尔卡特 (OT-927) 炭黑 联通3G手机 双卡双待，品牌家规格信息
				String title=goods.getGoods().getGoodsName();
				//把json字符串转换为对象
				Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
				//获取map集合中所有的值
				Collection values = map.values();
				//遍历值
				for (Object value : values) {
					title += value+" ";
				}
				//设置属性值
				tbItem.setTitle(title);
				setItemValue(tbGoods,goodsDesc,tbItem);
				//保存
				itemMapper.insert(tbItem);
			}
		}else {//没有被选中，我们自己构造一个SKU规格列表信息

			//创建对象
			TbItem tbItem = new TbItem();
			//设置标题等于商品KPU+规格描述串作为sku
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem.setPrice( goods.getGoods().getPrice() );//价格
			tbItem.setStatus("1");//状态
			tbItem.setIsDefault("1");//是否默认
			tbItem.setNum(99999);//库存数量
			tbItem.setSpec("{}");//没有sku给一个空的大括号
			//调用方法设置其他属性值
			setItemValue(tbGoods,goodsDesc,tbItem);
			//保存
			itemMapper.insert(tbItem);
		}


	}

	/**
	 * 商品状态修改
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids,String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 进行逻辑删除修改isDelete的值，代表删除
	 * @param ids
	 */
	@Override
	public void uplateIsDelete(Long[] ids) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 实现商品的上下架，修改isMarketable值为0（代表下架）或1（上架）
	 * @param ids
	 */
	@Override
	public void uplateIsMarketable(Long[] ids,String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsMarketable(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 通过goodsId查询Tbitem（sku列表）全部的信息
	 * @param ids
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findByGoodsIdTbitem(Long[] ids, String status) {
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		criteria.andGoodsIdIn(Arrays.asList(ids));
		return itemMapper.selectByExample(example);
	}


	/**
	 * 定义一个私有的方法来完成item属性的设置
	 * @param tbGoods
	 * @param goodsDesc
	 * @param tbItem
	 */
	private void setItemValue(TbGoods tbGoods,TbGoodsDesc goodsDesc,TbItem tbItem){
		//设值商品spu编号
		tbItem.setGoodsId(tbGoods.getId());
		//设置商家用户名
		tbItem.setSellerId(tbGoods.getSellerId());
		//设置商品分类编号
		tbItem.setCategoryid(tbGoods.getCategory3Id());
		//设置添加时间
		tbItem.setCreateTime(new Date());
		//设置修改时间
		tbItem.setUpdateTime(new Date());
		//设置品牌名称
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
		tbItem.setBrand(tbBrand.getName());
		//设置商家名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
		tbItem.setSeller(tbSeller.getNickName());
		//设置图片地址
		List<Map> list = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
		if (list.size()!=0){
			tbItem.setImage((String) list.get(0).get("url"));
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		TbGoods tbGoods = goods.getGoods();//获取商品基本信息
		goodsMapper.updateByPrimaryKey(tbGoods);//修改商品基本信息
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();//获取扩展信息
		goodsDescMapper.updateByPrimaryKey(goodsDesc);//修改扩展信息

		//先删除sku列表信息
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(tbGoods.getId());
		itemMapper.deleteByExample(example);
		//删除完成之后重新添加信息
		//判断规格是否启动
		if ("1".equals(tbGoods.getIsEnableSpec())){
			//获取sku列表信息
			List<TbItem> itemList = goods.getItemList();
			//遍历
			for (TbItem tbItem : itemList) {
				//设置item表中的title；格式为new2 - 阿尔卡特 (OT-927) 炭黑 联通3G手机 双卡双待，品牌家规格信息
				String title=goods.getGoods().getGoodsName();
				//把json字符串转换为对象
				Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
				//获取map集合中所有的值
				Collection values = map.values();
				//遍历值
				for (Object value : values) {
					title += value;
				}
				//设置属性值
				tbItem.setTitle(title);
				setItemValue(tbGoods,goodsDesc,tbItem);
				//保存
				itemMapper.insert(tbItem);
			}
		}else {//没有被选中，我们自己构造一个SKU规格列表信息

			//创建对象
			TbItem tbItem = new TbItem();
			//设置标题等于商品KPU+规格描述串作为sku
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem.setPrice( goods.getGoods().getPrice() );//价格
			tbItem.setStatus("1");//状态
			tbItem.setIsDefault("1");//是否默认
			tbItem.setNum(99999);//库存数量
			tbItem.setSpec("{}");//没有sku给一个空的大括号
			//调用方法设置其他属性值
			setItemValue(tbGoods,goodsDesc,tbItem);
			//保存
			itemMapper.insert(tbItem);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//创建商品信息组合实体类
		Goods goods = new Goods();
		//查询商品的基本信息
		goods.setGoods(goodsMapper.selectByPrimaryKey(id));
		//查询扩展信息
		goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
		//查询sku信息
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		//查询和goodsId等于id的数据
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);
		return goods;
	}


	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除品牌信息
			goodsMapper.deleteByPrimaryKey(id);
			//删除扩展信息
			goodsDescMapper.deleteByPrimaryKey(id);
			//删除规格列表信息sku
			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			itemMapper.deleteByExample(example);

		}		
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		//显示数据的时候排除逻辑状态修改删除的数据
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}


}
