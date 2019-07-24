package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.IfFunction;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemSerachServiceImpl implements ItemSerachService {


    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    @Override
    public Map search(Map searchMap) {
        Map map= new HashMap();
        //调用高亮查询方法
        Map map1 = searchMap1(searchMap);
        //追加到map集合中
        map.putAll(map1);

        //调用分组结果方法
        List<String> categoryList = categoryList(searchMap);
        map.put("categoryList",categoryList);

        //获取分类名称
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)){//分类不为空
            Map brandListAndSpecList = getBrandListAndSpecList(categoryName);
            map.putAll(brandListAndSpecList);
        }else{
            //当分类列表长度大于0执行方法
            if (categoryList.size()>0){
                Map brandListAndSpecList = getBrandListAndSpecList(categoryList.get(0));
                map.putAll(brandListAndSpecList);
            }
        }

        return map;
    }


    /**
     * 搜索查询及高亮显示
     * @return
     */
    private Map searchMap1(Map searchMap){
        Map map = new HashMap();
        //创建查询条件对象
        HighlightQuery query = new SimpleHighlightQuery();

        //1.1按照关键词查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按照分类查询
        if (!"".equals(searchMap.get("category"))){//判断是否有分类

            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3按照品牌查询
        if (!"".equals(searchMap.get("brand"))){//判断是否有品牌
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4按照规格查询
        if (searchMap.get("spec")!=null){
            //获取规格列表
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            //遍历key的值
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6价格设置查询
        String price = (String) searchMap.get("price");
        if(!"".equals(price)){//价格存在时
            String[] priceStr = price.split("-");
            //获取最大值和最小值
            String minPrice = priceStr[0];
            String maxPrice= priceStr[1];
            if (!minPrice.equals("0")){
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(minPrice);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            if ("*".equals(maxPrice)){
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(minPrice);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.7分页查询
        //设置分页参数
        Integer currentPage = (Integer) searchMap.get("currentPage");
        if (currentPage==null){
            currentPage=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页显示的条数
        if (pageSize==null){
            pageSize=20;
        }
        //设置起始索引
        query.setOffset((currentPage-1)*pageSize);
        //设置每页显示的条数
        query.setRows(pageSize);


        /*
        ########################### 高亮查询设置 ###################################
         */
        //设置高亮的域
        HighlightOptions highlightQuery =new HighlightOptions();
        highlightQuery.addField("item_title");//给标题域设置
        //设置高亮前缀
        highlightQuery.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highlightQuery.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightQuery);


        //高亮分页查询
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();//高亮数据的集合入口
        for (HighlightEntry<TbItem> entry : highlighted) {
            //获取原实体类
            TbItem tbItem = entry.getEntity();
            //entry.getHighlights().get(0).getSnipplets()获取第一个高亮域的内容
            //entry.getHighlights().get(0).getSnipplets().get(0) 一个高亮域中可能存在多值
            //判断高亮区域
            if (entry.getHighlights().get(0).getSnipplets().size()>0 && entry.getHighlights().size()>0){
                //设置title，修改为高亮区域
                tbItem.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        //给map集合添加高亮数据
        map.put("rows",page.getContent());
        //设置总页数
        map.put("totalPages",page.getTotalPages());
        //设置总记录数
        map.put("total",page.getTotalElements());
        return map;
    }


    /**
     * 品牌的分组查询
     * @return
     */
    private List<String> categoryList(Map searchMap){
        List<String> categoryList=new ArrayList<>();
        //定义查询对象
        Query query= new SimpleQuery();
        //设置查询条件
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项对象
        GroupOptions groupOptions=new GroupOptions();
        //设置分组查询条件
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //获取分组页数据
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组结果入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //遍历集合
        for (GroupEntry<TbItem> entry : content) {
            //将分组的结果封装到集合中
            categoryList.add(entry.getGroupValue());
        }

        return categoryList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 通过分类名称获取品牌列表和规格列表
     * @param categoryName
     * @return
     */
    private Map getBrandListAndSpecList(String categoryName){
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCatList").get(categoryName);

        if (typeId !=null){
            //通过id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            //添加到集合中
            map.put("brandList",brandList);
            //根据id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            //添加到map集合中
            map.put("specList",specList);
        }

        return map;
    }
}
