package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {


    @Reference
    private AddressService addressService;


    @RequestMapping("/findAddressListByUsername.do")
    public List<TbAddress> findAddressListByUsername(){
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TbAddress> addressList = addressService.findAddressListByUsername(name);
        return addressList;
    }

    /**
     * 添加地址
     * @param address
     */
    @RequestMapping("/addAddress.do")
    public Result addAddress(@RequestBody TbAddress address) {
        try {
            addressService.addAddress(address);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }


    /**
     * 修改地址
     * @param address
     */
    @RequestMapping("updateAddress.do")
    public Result updateAddress(@RequestBody TbAddress address){
        try {
            addressService.updateAddress(address);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }
}
