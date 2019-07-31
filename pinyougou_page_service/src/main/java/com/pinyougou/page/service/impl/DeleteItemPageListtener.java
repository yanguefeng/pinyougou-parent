package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


/**
 * 接收订阅消息类
 */
@Component
public class DeleteItemPageListtener implements MessageListener {


    @Autowired
    private ItemPageService itemPageService;

    /**
     * 生成商品详情页面的订阅消息的执行
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            //接收商品id
            Long[] goodsid = (Long[]) objectMessage.getObject();
            itemPageService.deleteItemHtml(goodsid);
            System.out.println("完成了");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
