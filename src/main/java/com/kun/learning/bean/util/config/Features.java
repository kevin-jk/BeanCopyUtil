package com.kun.learning.bean.util.config;


import com.kun.learning.bean.util.convert.BeanCopyConvert;
import com.kun.learning.bean.util.defaultConvetor.BigDecimal2StringConvetor;
import com.kun.learning.bean.util.defaultConvetor.Date2StringConvetor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jrjiakun on 2019/1/4
 *
 * 默认的转换方式
 *
 * BigDecimal 转成 String {@link BigDecimal2StringConvetor}
 *
 * Date 转成 String {@link Date2StringConvetor}
 *
 */
public enum Features {
    BigDecimal2String,
    Date2String,
    ;

    private static Map<Features, BeanTypeConfigHolder> featureConfigHolder = new HashMap() {{
        put(Features.BigDecimal2String, new BeanTypeConfigHolder(BigDecimal.class, String.class));
        put(Features.Date2String, new BeanTypeConfigHolder(Date.class, String.class));
    }};



    private static Map<Features, BeanCopyConvert> featureConvert = new HashMap<Features, BeanCopyConvert>() {{
        put(BigDecimal2String, new BigDecimal2StringConvetor());
        put(Date2String, new Date2StringConvetor());
    }};


    public static BeanTypeConfigHolder getHolder(Features features) {
        return featureConfigHolder.get(features);
    }

    public static BeanCopyConvert getConvert(Features features) {
        return featureConvert.get(features);
    }
}
