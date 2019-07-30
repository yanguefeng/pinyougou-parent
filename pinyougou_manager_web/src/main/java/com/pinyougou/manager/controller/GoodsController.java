package com.pinyougou.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.xml.soap.Text;

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
	public Result add(@RequestBody TbGoods goods){
		try {
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
		return goodsService.findPage(goods, page, rows);		
	}

	@Autowired
	private JmsTemplate jmsTemplate;


	@Autowired
	private Destination activeMQQueueUpdate;//用于发送更新solr的消息

	@Autowired
	private Destination activeMQTopicProductHtml;//用于发送生成商品html的消息

	/**
	 * 商品的审核
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			if ("1".equals(status)){
				List<TbItem> itemList = goodsService.findByGoodsIdTbitem(ids, status);
				if (itemList.size()>0){
					//当集合中有数据的时候添加到solr索引库中
					//itemSerachService.importItemToSolr(itemList);
					//调用消息中间件发送消息
					String text = JSON.toJSONString(itemList);
					jmsTemplate.send(activeMQQueueUpdate, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(text);
						}
					});
				}
				for (Long goodsid : ids) {
					//商品详情模板生成,调用消息中间件发送消息
					jmsTemplate.send(activeMQTopicProductHtml, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createObjectMessage(goodsid);
						}
					});
				}
			}
			return new Result(true,"审核通过成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"审核通过失败");
		}
	}


	@Autowired
	private Destination activeMQQueueDelete;

	/**
	 * 实现逻辑删除
	 * @param ids
	 */
	@RequestMapping("/uplateIsDelete.do")
	public Result uplateIsDelete(Long[] ids){
		try {
			goodsService.uplateIsDelete(ids);
			//当后台删除数据的时候要删除对应的在solr库中的数据
			//itemSerachService.deleteToSolrItemList(ids);
			//使用activemq完成删除是solr的商品信息的删除
				jmsTemplate.send(activeMQQueueDelete, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			return new Result(true,"删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}


	/**
	 * 生成模板
	 * @param goodsid
	 */
	/*@RequestMapping("/genHtml.do")
	public void genHtml(Long goodsid){
		itemPageService.getItemHtml(goodsid);
	}*/
}
