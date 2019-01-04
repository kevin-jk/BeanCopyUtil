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
 */
public enum Features {
    BigDecimal2String,
    Date2String,;

    private Map<Features, BeanTypeConfigHolder> featureConfigHolder = new HashMap() {{
        put(BigDecimal2String, new BeanTypeConfigHolder(BigDecimal.class, String.class));
        put(Date2String, new BeanTypeConfigHolder(Date.class, String.class));
    }};

    private Map<Features, BeanCopyConvert> featureConvert = new HashMap<Features, BeanCopyConvert>() {{
        put(BigDecimal2String, new BigDecimal2StringConvetor());
        put(Date2String, new Date2StringConvetor());
    }};

    public BeanTypeConfigHolder getHolder(Features features) {
        return featureConfigHolder.get(features);
    }

    public BeanCopyConvert getConvert(Features features) {
        return featureConvert.get(features);
    }
}
