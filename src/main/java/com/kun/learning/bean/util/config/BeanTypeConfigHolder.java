package com.kun.learning.bean.util.config;

/**
 * Created by jrjiakun on 2019/1/4
 *
 * 按照类型转换时候需要
 *
 * srcClass 表示源类型
 * desClass 表示目标类型
 *
 * 仅仅在属性copy的时候，源类型和目标类型定义一直的时候，才会进行属性copy
 *
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


    // 重写equals方法的时候必须重写hashCode方法，声明相等对象必须具有相等的哈希码
    //(1)当obj1.equals(obj2)为true时，obj1.hashCode() == obj2.hashCode()必须为true
    //(2)当obj1.hashCode() == obj2.hashCode()为false时，obj1.equals(obj2)必须为false
    @Override
    public boolean equals(Object obj) {
        if(this==obj){
            return true;
        }
        if(! (obj instanceof BeanTypeConfigHolder) ){
            return false;
        }
        BeanTypeConfigHolder holder = (BeanTypeConfigHolder)obj;
        if(null!=desClass&&null!=srcClass){
            return desClass.equals(holder.getDesClass()) && srcClass.equals(holder.getSrcClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result*31 + srcClass.hashCode();
        result = result*31 + desClass.hashCode();
        //super.hashCode每次不一样  why?
        // Object 的hashCode是一个native方法，其返回值是一个和对象存储地址有关联的int类型。hashCode方法是将对象的存储地址进行映射
        // 源码位置：hotspot/src/share/vm/runtime/synchronizer.cpp
      // result = result + super.hashCode();
        return result;
    }
}
