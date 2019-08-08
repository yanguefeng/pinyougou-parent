package com.pinyougou.service;

import com.pinyougou.pojo.TbAddress;


import java.util.List;

public interface AddressService {


    /**
     * 通过登录用名查询地址信息
     * @param username
     * @return
     */
    public List<TbAddress> findAddressListByUsername(String username);

    /**
     * 添加地址
     * @param address
     */
    void addAddress(TbAddress address);

    /**
     * 修改地址
     * @param address
     */
    void updateAddress(TbAddress address);
}
