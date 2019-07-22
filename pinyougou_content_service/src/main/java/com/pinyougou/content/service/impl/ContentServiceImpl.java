package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import com.pinyougou.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {

		contentMapper.insert(content);
		//添加之后清除缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询修改之前的id
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//清除之前的缓存
		redisTemplate.boundHashOps("content").delete(categoryId);
		contentMapper.updateByPrimaryKey(content);

		//只有修改前后的categoryId不相同的时候执行
		if (categoryId.longValue()!=content.getCategoryId().longValue()){
			//修改之后清除缓存
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//先通过id查询对象的categoryId
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			//获取categoryId
			Long categoryId = tbContent.getCategoryId();
			//清除缓存
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 根据categoryId查询数据
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {

		//从缓存中查询数据
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);

		//判断吧是否有数据
		if (list==null){

			//没有数据执行查询数据库，添加到缓存中
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			//判断轮播广告图片是否开启
			criteria.andStatusEqualTo("1");
			//根据sort_order排序
			example.setOrderByClause("sort_order");
			list = contentMapper.selectByExample(example);
			//往缓存中添加数据
			redisTemplate.boundHashOps("content").put(categoryId,list);
			return list;
		}
		return list;
	}

}
