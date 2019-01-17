package com.kun.learning.bean.util.defaultConvetor;

import com.kun.learning.bean.util.convert.BeanCopyConvert;

import java.math.BigDecimal;

/**
 * Created by jrjiakun on 2018/12/27
 *
 * BigDecimal转化为String
 *
 * 转换格式为： 去掉小数点后面无效的0
 */
public class BigDecimal2StringConvetor implements BeanCopyConvert<String,BigDecimal> {
   @Override
    public String convert(BigDecimal src) {
       return src==null?null:src.stripTrailingZeros().toPlainString();
    }
}
