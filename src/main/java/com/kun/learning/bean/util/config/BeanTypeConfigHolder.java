package com.kun.learning.bean.util.config;

/**
 * Created by jrjiakun on 2019/1/4
 */
public class BeanTypeConfigHolder {
    Class srcClass;
    Class desClass;

    public BeanTypeConfigHolder() {

    }

    public BeanTypeConfigHolder(Class srcClass, Class desClass) {
        this.srcClass = srcClass;
        this.desClass = desClass;
    }

    public Class getSrcClass() {
        return srcClass;
    }

    public void setSrcClass(Class srcClass) {
        this.srcClass = srcClass;
    }

    public Class getDesClass() {
        return desClass;
    }

    public void setDesClass(Class desClass) {
        this.desClass = desClass;
    }


}
