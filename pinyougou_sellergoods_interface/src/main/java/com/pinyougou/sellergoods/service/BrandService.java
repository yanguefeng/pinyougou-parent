package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param currentPage 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    public PageResult findPage(int currentPage,int pageSize);


    /**
     * 增加数据
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 通过id查询
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改品牌信息
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 删除品牌信息
     * @param ids
     */
    public void delete(Long[] ids);


    /**
     * 分页模糊查询
     * @param currentPage 当前页
     * @param pageSize 每页显示条数
     * @return
     */
    public PageResult findPage(TbBrand brand,int currentPage,int pageSize);

    /**
     * 查询品牌名称和id
     * @return
     */
    public List<Map> selectOptionList();
}
