package com.kun.learning.bean.test;

import com.kun.learning.bean.test.bean.A;
import com.kun.learning.bean.test.bean.C;
import com.kun.learning.bean.util.BeanUtil;
import com.kun.learning.bean.util.convert.BeanCopyConvert;
import com.kun.learning.bean.util.defaultConvetor.BigDecimal2StringConvetor;
import com.kun.learning.bean.util.defaultConvetor.Date2StringConvetor;
import com.kun.learning.bean.util.em.BeanCopyConfigEm;
import org.junit.Test;

import java.util.HashMap;


/**
 * Created by jrjiakun on 2018/12/27
 */
public class BeanUtilTest extends TestBase{

    BeanUtil beanUtil = new BeanUtil(new HashMap<BeanCopyConfigEm, BeanCopyConvert>(){{
        put(BeanCopyConfigEm.BigDecimalConfig,new BigDecimal2StringConvetor());
        put(BeanCopyConfigEm.DateConfig,new Date2StringConvetor());
    }});

    @Test
    public void test_copy(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        beanUtil.copyProperties(a,c);
        System.out.println(c);
    }
}
