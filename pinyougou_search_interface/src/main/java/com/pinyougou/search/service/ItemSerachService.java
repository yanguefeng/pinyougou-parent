package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSerachService {

    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    public Map search(Map searchMap);


    /**
     * 通过goods的id删除solr索引库中的对应的数据
     * @param ids
     */
    public void deleteToSolrItemList(Long[] ids);

    /**
     * 更新solr库中数据
     * @param list
     * @return
     */
    public void importItemToSolr(List list);
}
