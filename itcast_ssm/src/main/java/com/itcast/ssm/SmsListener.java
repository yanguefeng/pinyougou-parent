package com.itcast.ssm;


import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 给用户发送短信类
 */
@Component
public class SmsListener {


    @Autowired
    private SmsUtils smsUtils;

    /**
     * 接收消息发送短信
     * @param map
     */
    @JmsListener(destination = "pinyougou_queue_sms")
    public void sendSms(Map<String,String> map){
        try {
            SendSmsResponse response = smsUtils.sendSms(map.get("phoneNumber"),
                    map.get("signName"), map.get("templateCode"), map.get("param"));

            System.out.println(map);
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
