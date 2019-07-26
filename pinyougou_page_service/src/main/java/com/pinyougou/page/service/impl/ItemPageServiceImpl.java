package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {


    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

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
            Template template = configuration.getTemplate("");
            //创建数据源
            Map dataModel=new HashMap();
            //读取商品数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsid);
            dataModel.put("tbgoods",tbGoods);
            //读取商品扩展信息
            TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsid);
            dataModel.put("tbGoodsDesc",tbGoodsDesc);
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
}
