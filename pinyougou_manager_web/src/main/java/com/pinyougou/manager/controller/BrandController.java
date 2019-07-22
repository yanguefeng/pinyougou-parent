package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
        List<TbBrand> brandList = brandService.findAll();
        return brandList;
    }

    /**
     * 返回全部列表
     * @return
     */
    @RequestMapping("/findPage.do")
    public PageResult findPage(int currentPage,int pageSize){
        PageResult pageResult = brandService.findPage(currentPage, pageSize);
        return pageResult;
    }

    /**
     * 增加
     * @param brand
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 获取实体
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改
     * @param brand
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }


    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }


    /**
     * 查询+分页
     * @param
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(@RequestBody TbBrand brand, int currentPage,int pageSize){
        PageResult pageResult = brandService.findPage(brand,currentPage, pageSize);
        return pageResult;
    }

    /**
     * 品牌下拉列表
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    };
}
