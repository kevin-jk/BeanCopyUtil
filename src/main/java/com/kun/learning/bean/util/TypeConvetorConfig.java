package com.kun.learning.bean.util;

import com.kun.learning.bean.util.convert.BeanCopyConvert;

/**
 * Created by jrjiakun on 2018/12/27
 */
public class TypeConvetorConfig {
    private Class srcType;
    private Class desType;

    private BeanCopyConvert beanCopyConvert;

    public Class getSrcType() {
        return srcType;
    }

    public void setSrcType(Class srcType) {
        this.srcType = srcType;
    }

    public Class getDesType() {
        return desType;
    }

    public void setDesType(Class desType) {
        this.desType = desType;
    }

    public BeanCopyConvert getBeanCopyConvert() {
        return beanCopyConvert;
    }

    public void setBeanCopyConvert(BeanCopyConvert beanCopyConvert) {
        this.beanCopyConvert = beanCopyConvert;
    }
}
