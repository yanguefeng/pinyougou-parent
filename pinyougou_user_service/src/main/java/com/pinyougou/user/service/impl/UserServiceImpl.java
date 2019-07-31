package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.UserService;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private TbUserMapper userMapper;


    /**
     * 用户注册方法
     * @param user
     */
    @Override
    public void register(TbUser user) {
        user.setCreated(new Date());//设置创建时间
        user.setUpdated(new Date());//设置修改时间
        user.setSourceType("1");//1代表pc端
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));//对密码加密
        userMapper.insert(user);
    }


    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private ActiveMQQueue activeMQQueueSms;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${signName}")
    private String signName;

    @Value("${templateCode}")
    private String templateCode;


    /**
     * 创建验证码
     * @param phone
     */
    @Override
    public void createSmsCode(String phone) {
        String code =(long)Math.random()*1000000+"";//产生6为随机的验证码
        //保存到redis缓存中
        redisTemplate.boundHashOps("smscode").put(phone,code);

       jmsTemplate.send(activeMQQueueSms, new MessageCreator() {
           @Override
           public Message createMessage(Session session) throws JMSException {
               MapMessage message = session.createMapMessage();
               message.setString("phoneNumber",phone);
               message.setString("signName",signName);
               message.setString("templateCode",templateCode);
               Map<String, String> stringMap = new HashMap<>();
               stringMap.put("param",code);
               message.setString("param", JSON.toJSONString(stringMap));
               return message;
           }
       });
    }

}
