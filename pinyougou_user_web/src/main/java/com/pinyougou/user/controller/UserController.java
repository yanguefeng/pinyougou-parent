package com.pinyougou.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.UserService;

import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController {


    @Reference
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 注册用户
     * @param user
     * @return
     */
    @RequestMapping("/register.do")
    @ResponseBody
    public Result register(@RequestBody TbUser user ,String htmlCode){
        String smscode = (String) redisTemplate.boundHashOps("smscode").get(user.getPhone());//获取缓存中的验证码
        if (smscode ==null && !smscode.equals(htmlCode)){//验证码不相等返回错误信息
            return new Result(false,"验证码有误");
        }
        try {
            userService.register(user);
            return new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }


    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @RequestMapping("/createSmsCode.do")
    @ResponseBody
    public Result createSmsCode(String phone){
        //对电话号码验证
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){//电话号码格式错误直接返回错误信息
            return new Result(false,"手机号码格式有问题");
        }
        try {
            userService.createSmsCode(phone);
            return new Result(true,"验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"验证码发送失败");
        }
    }
}
