package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {


    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pagedir}")
    private String pagedir;
    /**
     * 生成html页面
     * @param goodsid
     * @return
     */
    @Override
    public boolean getItemHtml(Long goodsid) {

        try {
            //创建配置类
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //读取模板文件
            Template template = configuration.getTemplate("item.ftl");
            //创建数据源
            Map dataModel=new HashMap();
            //读取商品数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsid);
            dataModel.put("tbgoods",tbGoods);
            //读取商品扩展信息
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsid);
            dataModel.put("tbGoodsDesc",tbGoodsDesc);

            //获取分类信息
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //获取sku信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            //状态为1的为有效
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(tbGoods.getId());
            example.setOrderByClause("is_default desc");//按照状态排序，保证第一个为默认状态1
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);

            //获取输出流
            Writer out=new FileWriter(pagedir+goodsid+".html");
            template.process(dataModel,out);
            //关闭输出
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 删除商品详情页
     * @param ids
     * @return
     */
    @Override
    public boolean deleteItemHtml(Long[] ids) {

        try {
            for (Long goodsid : ids) {
                File file = new File(pagedir + goodsid + ".html");
                file.delete();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
