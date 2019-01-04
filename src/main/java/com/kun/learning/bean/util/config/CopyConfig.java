package com.kun.learning.bean.util.config;

import com.kun.learning.bean.util.convert.BeanCopyConvert;

/**
 * Created by jrjiakun on 2019/1/4
 *
 * 用户自定义转换方式
 *
 * 有2种方式定义：
 * 1. 按照类型转换
 * 2. 按照字段名转换
 *
 *  如果按照类型转换，X 必须为 {@link BeanTypeConfigHolder}类型
 *
 *  如果按照字段名转化，X必须为{@link String}类型
 *
 */
public class CopyConfig<X> {
    X key;
    BeanCopyConvert value;

    public X getKey() {
        return key;
    }

    public void setKey(X key) {
        this.key = key;
    }

    public BeanCopyConvert getValue() {
        return value;
    }

    public void setValue(BeanCopyConvert value) {
        this.value = value;
    }
}
