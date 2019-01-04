package com.kun.learning.bean.util.convert;

/**
 * 属性copy转换器
 *
 * J: 目标对象属性对应的类型
 * K: 源对象对应属性对应的类型
 * */
public interface BeanCopyConvert<J,K> {
    J convert(K src);
}
