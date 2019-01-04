package com.kun.learning.bean.util.defaultConvetor;

import com.kun.learning.bean.util.convert.BeanCopyConvert;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jrjiakun on 2018/12/27
 *
 * Date转String转换器
 */
public class Date2StringConvetor implements BeanCopyConvert<String,Date> {
    private static SimpleDateFormat default_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String convert(Date src) {
        return null==src?null:default_sdf.format(src);
    }
}
