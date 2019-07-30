package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 消息接收类
 */
@Component
public class ItemSearchUpdateSolrListener implements MessageListener {

    @Autowired
    private ItemSerachService itemSerachService;

    /**
     * 接收消息更新到solr索引库
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            String text = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            itemSerachService.importItemToSolr(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
