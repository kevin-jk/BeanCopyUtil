package com.kun.learning.bean.test;

import com.kun.learning.bean.test.bean.A;
import com.kun.learning.bean.test.bean.C;
import com.kun.learning.bean.util.BeanUtil;
import com.kun.learning.bean.util.config.BeanTypeConfigHolder;
import com.kun.learning.bean.util.config.CopyConfig;
import com.kun.learning.bean.util.config.Features;
import com.kun.learning.bean.util.convert.BeanCopyConvert;
import com.kun.learning.bean.util.defaultConvetor.BigDecimal2StringConvetor;
import com.kun.learning.bean.util.defaultConvetor.Date2StringConvetor;
import com.kun.learning.bean.util.exception.BeanCopyException;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by jrjiakun on 2018/12/27
 */
public class BeanUtilTest extends TestBase{


    @Test
    public void test_copy(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        BeanUtil.copyProperties(a,c, Features.BigDecimal2String);
        System.out.println(c);
        BeanUtil.copyProperties(a,c, Features.Date2String,Features.BigDecimal2String);
    }

    @Test(expected = BeanCopyException.class)
    public void test_NotSameTypeField(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        BeanUtil.copyProperties(a,c);
    }

    @Test(expected = BeanCopyException.class)
    public void test_Feature_null(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
     //   BeanUtil.copyProperties(a,c,Features.Date2String);
        BeanUtil.copyProperties(a,c,(Features)null);
    }

    @Test(expected = BeanCopyException.class)
    public void test_Config_null(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        //   BeanUtil.copyProperties(a,c,Features.Date2String);
        BeanUtil.copyProperties(a,c,(CopyConfig)null);
    }

    @Test(expected = BeanCopyException.class)
    public void test_ignoreField(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        BeanUtil.copyProperties(a,c,"a");
    }


    @Test(expected = BeanCopyException.class)
    public void test_ignoreField_null(){
        A a = new A();
        a.setA("a");
        a.setAs("as");
        C c = new C();
        BeanUtil.copyProperties(a,c,(String)null);
    }


    @Test
    public void test_bigDecimal(){
        BigDecimal bigDecimal = new BigDecimal("0");
        BigDecimal bigDecimal1 = new BigDecimal("0.0");
        BigDecimal bigDecimal2 = new BigDecimal("0.00");
        BigDecimal bigDecimal4 = new BigDecimal("9.00244000");
        System.out.println( bigDecimal1.compareTo(bigDecimal2));

        BigDecimal2StringConvetor bigDecimal2StringConvetor = new BigDecimal2StringConvetor();
        System.out.println(bigDecimal2StringConvetor.convert(bigDecimal));
        System.out.println(bigDecimal2StringConvetor.convert(bigDecimal1));
        System.out.println(bigDecimal2StringConvetor.convert(bigDecimal2));
        System.out.println(bigDecimal2StringConvetor.convert(bigDecimal4));
    }
}
