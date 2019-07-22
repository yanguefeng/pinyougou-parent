package com.pinyougou.solr.util;


import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * 数据库数据导入solr库工具类
 */
@Component
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class SolrUtils {

    @Autowired
    private TbItemMapper itemMapper;


    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 数据库数据导入solr库中
     */
    @Test
    public  void importDataSolr(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map map = JSON.parseObject(spec, Map.class);
            tbItem.setMap(map);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }


}
