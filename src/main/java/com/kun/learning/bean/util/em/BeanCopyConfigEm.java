package com.kun.learning.bean.util.em;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 已经废弃
 * */
@Deprecated
public enum BeanCopyConfigEm {
    BigDecimalConfig(BigDecimal.class.getName(),BigDecimal.class),
    DateConfig(Date.class.getName(),Date.class),
;


    private String configType;
    private Class clazz;

    BeanCopyConfigEm(String code, Class clazz) {
        this.configType = code;
        this.clazz = clazz;
    }

    public static BeanCopyConfigEm getByConfigType(String configType){
        for (BeanCopyConfigEm temp: BeanCopyConfigEm.values()){
            if(temp.getConfigType().equals(configType)){
                return temp;
            }
        }
        return  null;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
