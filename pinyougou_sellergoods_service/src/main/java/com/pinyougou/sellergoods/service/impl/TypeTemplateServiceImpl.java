package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}


	/**
	 * 分页以及条件分页查询
	 * @param typeTemplate
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();

		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 模板下拉列表
	 * @return
	 */
	@Override
	public List<Map> selectOptionList() {
		return typeTemplateMapper.selectOptionList();
	}


	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	/**
	 * 规格名称和规格列表的信息；在模板表中只有规格名称的信息，我们需要通过规格id查询对应的规格列表信息，从而添加到
	 * map集合中，使map集合中拥有规格名称和规格列表的完整信息；
	 * @param template
	 * @return
	 */
	@Override
	public List<Map> findSpecList(Long template) {
		//通过模板id查询信息
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(template);
		//把模板信息转换为集合采用json的方法parseArray中的两个参数的方法
		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		//遍历集合
		for (Map map : list) {
			//获取map集合中的规格id
			Integer id = (Integer) map.get("id");
			//通过规格id去查询谷歌列表
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			//创建条件，使其查询的条件等于获取的id
			criteria.andSpecIdEqualTo(new Long(id));
			List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
			//把获取到的数据添加到map集合中
			map.put("option",specificationOptionList);
		}

		//返回集合
		return list;
	}

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 把数据存入到缓存中
	 */
	private void saveToRedis(){

		//获取全部的模板数据
		List<TbTypeTemplate> templateList = findAll();
		//循环模板
		for (TbTypeTemplate template : templateList) {
			//存储品牌到缓存
			List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
			//把模板id当做键，集合当做值
			redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);

			//将规格列表存储到缓存中
			//根据模板id查询规格列表,将模板id当做键，规格集合列表当做值
			List<Map> specList = findSpecList(template.getId());
			redisTemplate.boundHashOps("specList").put(template.getId(),specList);
		}
	}
}
