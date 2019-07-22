package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
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
        //设置高亮的域
        HighlightOptions highlightQuery =new HighlightOptions();
        highlightQuery.addField("item_title");//给标题域设置
        //设置高亮前缀
        highlightQuery.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highlightQuery.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightQuery);
        //按照关键词查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
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
}
