package com.itcast.ssm;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SmsSpringBootDemo {


    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;



    @RequestMapping("/sendnonl")
    public void send(){
        Map<String,String> map=new HashMap<String, String>();
        map.put("phoneNumber","18947762080");
        map.put("signName","乐享购");
        map.put("templateCode","SMS_171541307");
        map.put("param", "{\"code\":\"123456\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
