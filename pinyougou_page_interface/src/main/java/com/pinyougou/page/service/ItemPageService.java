package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 生成html页面
     * @param goodsid
     * @return
     */
    public boolean getItemHtml(Long goodsid);

    /**
     * 删除商品详情页
     * @param goodsid
     * @return
     */
    public boolean deleteItemHtml(Long[] goodsid);
}
