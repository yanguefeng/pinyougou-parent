package com.pinyougou.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSerachService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemSearcah")
public class ItemSearchController {

    @Reference
    private ItemSerachService itemSerachService;

    @RequestMapping("/search.do")
    public Map search(@RequestBody Map searchMap){
        Map map = itemSerachService.search(searchMap);
        return map;
    }


}
