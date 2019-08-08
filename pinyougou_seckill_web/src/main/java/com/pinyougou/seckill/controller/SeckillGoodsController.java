package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀商品信息
     * @return
     */
    public List<TbSeckillGoods> findList(){
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsService.findList();
        return seckillGoodsList;
    }


    /**
     * 从缓存中获取商品信息
     * @param id
     * @return
     */
    @RequestMapping("/findOneFromRedis.do")
    public TbSeckillGoods findOneFromRedis(Long id){
        TbSeckillGoods oneFromRedis = seckillGoodsService.findOneFromRedis(id);
        return oneFromRedis;
    }
}
