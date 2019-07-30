package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSerachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;

/**
 * 执行删除消息的监听类
 */
@Component
public class ItemSearchDeleteSolrListener implements MessageListener {

    @Autowired
    private ItemSerachService itemSerachService;


    /**
     * 接收消息删除solr索引库对应的信息
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            //获取id数组信息
            Long[] ids = (Long[]) objectMessage.getObject();
            itemSerachService.deleteToSolrItemList(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
