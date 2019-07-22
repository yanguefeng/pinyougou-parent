package cn.itcast;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-dao.xml")
public class Test01 {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Test
    public  void test01() {
        System.out.println(tbBrandMapper.selectByExample(null));


    }

//    @Test
//    public void test(){
//        TbItemCatExample example = new TbItemCatExample();
//        TbItemCatExample.Criteria criteria = example.createCriteria();
//        Long l=0L;
//        criteria.andParentIdEqualTo(l);
//        List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
//        System.out.println(tbItemCats);
//    }
}
