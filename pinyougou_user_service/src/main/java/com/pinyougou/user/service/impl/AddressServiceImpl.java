package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {


    @Autowired
    private TbAddressMapper addressMapper;

    /**
     * 通过登录用户名查询地址信息
     * @param username
     * @return
     */
    @Override
    public List<TbAddress> findAddressListByUsername(String username) {
        TbAddressExample example = new TbAddressExample();
        TbAddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(username);
        List<TbAddress> addressList = addressMapper.selectByExample(example);
        return addressList;
    }

    /**
     * 添加地址
     * @param address
     */
    @Override
    public void addAddress(TbAddress address) {
        addressMapper.insert(address);
    }

    /**
     * 修改地址
     * @param address
     */
    @Override
    public void updateAddress(TbAddress address) {
        addressMapper.updateByPrimaryKey(address);
    }


}
