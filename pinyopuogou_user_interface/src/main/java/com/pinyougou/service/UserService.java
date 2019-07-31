package com.pinyougou.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {


    /**
     * 用户注册
     * @param user
     */
    public void register(TbUser user);

    /**
     * 创建验证码
     * @param phone
     */
    void createSmsCode(String phone);
}
