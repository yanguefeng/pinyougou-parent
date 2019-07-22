package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.search.service.ItemSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

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
        return null;
    }
}
